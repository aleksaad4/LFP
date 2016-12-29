/**
 * Created by semyon on 04.07.16.
 */
import SockJS from "sockjs-client/js/sockjs-fixed";

function guid() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}

function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
}

const RECONNECT_TIMEOUT = 3000;

export default class WebSocketService {

    constructor(TabService) {
        var that = this;
        this.lastEventTimestamp = new Date().getTime();
        this.tabService = TabService;

        // данные для коннекта
        this.accountId = null;
        this.visitorId = null;

        this.ws = null;
        this.listeners = {
            ALL: []
        };

        that.listenMasterChanged = function (isMaster) {
            console.log("Master changed: " + isMaster);
            if (isMaster) {
                // переподключить сокет
                console.log("Reconnect socket");
                that.closeSocket();
                that.openSocket();

            } else {
                // отключить сокет
                console.log("Close socket");
                that.closeSocket();
            }
        };

        that.listenWsConnectMessage = function (broadcastMessage) {
            console.log("Handle WS Connect message: " + JSON.stringify(broadcastMessage));
            var data = broadcastMessage.data;
            var accId = data.accountId ? data.accountId : this.accountId;
            var visId = data.visitorId ? data.visitorId : this.visitorId;
            that.setIds(accId, visId, false);
        };

        that.listenWsDataMessage = function (broadcastMessage) {
            console.log("Handle WS Data message: " + JSON.stringify(broadcastMessage));
            that.handleMessage(broadcastMessage.data);
        };

        // подключим слушателей
        this.tabService.registerMasterChangeHandler("websocketService", that.listenMasterChanged);
        this.tabService.registerBroadcastHandler("wsconnect", that.listenWsConnectMessage);
        this.tabService.registerBroadcastHandler("wsdata", that.listenWsDataMessage);
    }

    setIds(accountId, visitorId, broadcast) {
        this.accountId = accountId;
        this.visitorId = visitorId;
        // переподключим сокет
        this.closeSocket();
        if (this.tabService.isMasterNow()) {
            console.log("This tab is master, connect socket");
            this.openSocket();
        } else {
            console.log("This tab is slave, don't connect socket");
            if (broadcast) {
                var data = {accountId: this.accountId, visitorId: this.visitorId};
                this.tabService.broadcast("wsconnect", data);
            }
        }
    }

    openSocket() {
        console.log("Open socket for account: " + this.accountId + " and visitor: " + this.visitorId);
        // варианты транспорта сообщений
        var trs = ['websocket', 'xdr-streaming', 'xhr-streaming', 'iframe-eventsource',
            'iframe-htmlfile', 'xdr-polling', 'xhr-polling', 'iframe-xhr-polling', 'jsonp-polling'];

        var socketUrl;
        if (this.accountId && this.visitorId) {
            socketUrl = '/socket/?type=both&accountId=' + this.accountId + "&visitorId=" + this.visitorId;
        } else if (this.accountId) {
            socketUrl = '/socket/?type=account&id=' + this.accountId;
        } else if (this.visitorId) {
            socketUrl = '/socket/?type=visitor&id=' + this.visitorId;
        } else {
            console.warn("Can't connect now - accountId and visitorId are absent");
            return;
        }

        if (this.lastEventTimestamp != null) {
            socketUrl += "&timestamp=" + this.lastEventTimestamp;
        }
        this.ws = new SockJS(socketUrl, null, {transports: trs});
        this.ws.onopen = this.onOpen.bind(this);
        this.ws.onmessage = this.onMessage.bind(this);
        this.ws.onclose = this.onClose.bind(this);
    }

    closeSocket() {
        console.log("Close socket");
        if (this.ws) {
            this.ws.close();
        }
    }


    handleMessage(event) {
        const eventType = event.type;
        this.lastEventTimestamp = event.timestamp;
        let listeners = this.listeners[eventType];
        if (listeners) {
            for (let i = 0; i < listeners.length; i++) {
                let listener = listeners[i];
                listener.fn(event);
            }
        }
        listeners = this.listeners['ALL'];
        for (let i = 0; i < listeners.length; i++) {
            let listener = listeners[i];
            listener.fn(event);
        }
    }

    subscribeOnEvents(handler, id) {
        if (!id) {
            id = guid();
        }
        var obj = handler;
        let eventType = obj.eventType;
        let fn = obj.fn;

        let eventTypes = eventType.split(",");
        for (var j = 0; j < eventTypes.length; j++) {
            var type = eventTypes[j];

            var eventListenersList = this.listeners[type];
            if (!eventListenersList) {
                eventListenersList = [];
                this.listeners[type] = eventListenersList;
            }
            eventListenersList.push({
                id: id,
                fn: fn
            });
        }

        return id;
    }

    subscribeOnAllEvents(handlerFn, id) {
        if (!id) {
            id = guid();
        }
        this.listeners['ALL'].push({
            id: id,
            fn: handlerFn
        });
    }

    removeListener(id) {
        Object.keys(this.listeners).forEach(function (lstKey) {
            var lst = this.listeners[lstKey];
            for (var i = lst.length - 1; i >= 0; i--) {
                var obj = lst[i];
                if (obj.id == id) {
                    lst.splice(i, 1);
                }
            }
        }.bind(this));
    }

    onOpen() {
    }

    onMessage(event) {
        console.log('WebSocket message event');
        event = JSON.parse(event.data);
        this.handleMessage(event);

        // теперь перешлем его по остальным вкладкам
        this.tabService.broadcast("wsdata", event);
    }

    onClose() {
        var that = this;
        console.error('WebSocket connection closed.');
        that.ws = null;

        // если эта вкладка Master - делаем reconnect
        if (that.tabService.isMasterNow()) {
            console.log("I am master, try to reconnect");
            setTimeout(function () {
                that.openSocket();
            }.bind(that), RECONNECT_TIMEOUT);
        } else {
            console.log("I am not master, don't try to reconnect");
        }
    }
}

WebSocketService.$inject = ["TabService"];
