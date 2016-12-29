export default class TabService {

    constructor() {
        var that = this;
        that.deleteDelay = 23000;
        that.firstCheckDelay = 500;
        that.checkInterval = 9000;
        that.pingInterval = 17000;

        // инициализируем экземпляр сервиса
        that.id = Math.random();
        that.isMaster = false;
        // здесь будем хранить ID экземпляров с других вкладок и timestamp'ы их последних ответов
        that.others = {};

        // сигнал регистрации нового экземпляра
        that.hello = function (event) {
            console.log("Hello handler from " + event.id + " own(" + that.id + ")");
            that.ping(event);
            // если ID нового участника меньше, чем собственный - возможно, мастер изменится
            if (event.id < that.id) {
                that.check();
            } else {
                // послать свой пинг, чтобы у нового участника появились данные обо мне
                that.broadcast("ping");
            }
        };

        // сигнал удаления экземпляра
        that.bye = function (event) {
            console.log("Bye handler from " + event.id + " own(" + that.id + ")");
            // удаляем экземпляр и проверяем, кто теперь мастер
            delete that.others[event.id];
            that.check();
        };

        // прием сигнала-пинга, обновление timestamp'а для данного экземпляра
        that.ping = function (event) {
            that.others[event.id] = Date.now();
        };

        // сюда включим функции-обработчики сигналов
        that.broadcastHandlers = {
            hello: that.hello,
            bye: that.bye,
            ping: that.ping
        };

        // включим функции-обработчики перехода роли мастера
        that.changeStateHandlers = {};

        // обработчик событий
        that.handleEvent = function (event) {
            // если вкладка выгружается - поймать это событие
            if (event.type === 'unload') {
                that.$destroy();
                // если пришло событие бродкаста
            } else if (event.key === 'broadcast') {
                try {
                    var data = JSON.parse(event.newValue);
                    // если событие не от себя самого - обработаем его
                    if (data.id !== that.id) {
                        // найти обработчик события, если он есть
                        var handlerFunction = that.broadcastHandlers[data.type];
                        if (handlerFunction) {
                            handlerFunction(data);
                        }
                    }
                } catch (error) {
                }
            }
        };

        // функция проверки timestamp'ов от всех остальных и выбора себя мастером
        that.check = function () {
            var now = Date.now();
            var takeMaster = true;

            for (let id in that.others) {
                // если какой-то экземпляр давно не апдейтил свою метку - удалим его данные
                if (that.others[id] + that.deleteDelay < now) {
                    delete that.others[id];

                    // у кого самый меньший ID - тот и станет новым мастером
                } else if (id < that.id) {
                    takeMaster = false;
                }

            }
            // если мастер сменился - вызовем метод masterDidChange
            if (that.isMaster !== takeMaster) {
                that.isMaster = takeMaster;
                that.masterDidChange();
            }
        };

        // функция, которая вызывается, когда мастер поменялся
        that.masterDidChange = function () {
            console.log(that.isMaster ? "I am master now" : "I am slave now");
            for (let handlerId in that.changeStateHandlers) {
                // разослать событие по всем слушателям
                try {
                    if (that.changeStateHandlers.hasOwnProperty(handlerId)) {
                        that.changeStateHandlers[handlerId](that.isMaster);
                    }
                } catch (error) {
                    console.error("Can't handle event of master change for handler: " + handlerId + ": " + error);
                }
            }
        };

        // в контроллере зарегистриуемся как слушатель событий изменения хранилищи и закрытия вкладки
        window.addEventListener('storage', that.handleEvent, false);
        window.addEventListener('unload', that.handleEvent, false);

        // разошлем свое приветсвенное сообщение
        that.broadcast("hello");

        // запустим задачи периодической проверки статуса и рассылки таймаутов
        var periodicalCheck = function () {
            that.check();
            that.checkTimeout = setTimeout(periodicalCheck, that.checkInterval);
        };

        var periodicalPing = function () {
            that.broadcast("ping");
            that.pingTimeout = setTimeout(periodicalPing, that.pingInterval);
        };

        that.checkTimeout = setTimeout(periodicalCheck, that.firstCheckDelay);
        that.pingTimeout = setTimeout(periodicalPing, that.pingInterval);
    };

    // деструктор сервиса
    $destroy() {
        var that = this;
        clearTimeout(that.pingTimeout);
        clearTimeout(that.checkTimeout);
        window.removeEventListener('storage', that.handleEvent, false);
        window.removeEventListener('unload', that.handleEvent, false);
        that.broadcast('bye');
    };

    isMasterNow(){
        return this.isMaster;
    }


// функция рассылки событий между вкладками
    broadcast(type, data) {
        var that = this;
        var event = {
            // от кого событие
            id: that.id,
            // тип события
            type: type,
            // данные события
            data: data
        };
        try {
            localStorage.setItem('broadcast', JSON.stringify(event));
        } catch (error) {
            console.error("Can't save event into local storage: " + error);
        }
    }

// зарегистрировать хэндлер событий
// хэндлер - это функция вида handle(event)
// у event'а есть type и data
// NOTE: на каждый eventType только один хэндлер
    registerBroadcastHandler(eventType, handler) {
        var that = this;
        console.log("Register new handler for event type: " + eventType);
        if (eventType != 'hello' && eventType != 'bye' && eventType != "ping") {
            that.broadcastHandlers[eventType] = handler;
        } else {
            alert("Try to register another system handler");
        }
    }

// удалить хэндлер событий
    deleteBroadcastHandler(eventType) {
        var that = this;
        console.log("Delete handler for event type: " + eventType);
        if (eventType != 'hello' && eventType != 'bye' && eventType != "ping") {
            delete that.broadcastHandlers[eventType];
        } else {
            alert("Try to delete system handler");
        }
    }

// зарегистрировать хэндлер изменения состояния
// хэндлер - функция вида handle(isMaster)
// handlerId - идентификатор хэндлера (по нему можно удалиться потом)
    registerMasterChangeHandler(handlerId, handler) {
        var that = this;
        that.changeStateHandlers[handlerId] = handler;
    }

// удалить хэндлер состояния по ID
    deleteMasterChangeHandler(handlerId) {
        var that = this;
        delete that.changeStateHandlers[handlerId];
    }
}
