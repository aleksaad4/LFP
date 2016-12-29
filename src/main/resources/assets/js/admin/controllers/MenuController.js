/**
 * Created by semyon on 17.06.16.
 */

export default class MenuController {

    constructor($scope, $rootScope, $state, AccountService, WebSocketService, notificationManager, Restangular, SnackbarService, $http) {
        var that = this;
        that.notificationManager = notificationManager;
        that.$rootScope = $rootScope;
        that.SnackbarService = SnackbarService;
        that.$state = $state;
        that.accountService = AccountService;
        that.newMessageCount = 0;
        that.$http = $http;

        this.menu = {
            visibility: false,
            selectedIndex: -1,
            selectedIndexLeft: -1,
            activeMenu: {},
            selectedSubmenuIndex: -1
        };
        this.timeoutStatusForm = {
            show: false,
            data: {}
        };

        var rCtrl = $scope.rCtrl;
        that.menus = rCtrl.menu;
        that.accountData = that.accountService.getAccountData();
        if (that.accountData && that.accountData.user) {
            var userAccount = that.accountData.user.account;
            that.isLikeOperator = userAccount.role.name == 'OPERATOR' || userAccount.role.name == 'EXPERT' || userAccount.role.name == 'SUPER_EXPERT';
            that.isOperator = userAccount.role.name == 'OPERATOR';
        } else {
            that.isLikeOperator = false;
            that.isOperator = false;
        }
        that.initMenu();


        // отображение количества сообщений
        that.messageCount = '?';
        const incomeMessage = (new Audio(require("url!../../../sounds/notification.mp3")));
        const dialogStarted = (new Audio(require("url!../../../sounds/beep_short_on.mp3")));
        const dialogFinished = (new Audio(require("url!../../../sounds/beep_short_off.mp3")));


        var subscriptionId = null;

        var classifiersDAO = Restangular.one("/account/conversation/topics/online/");
        subscriptionId = WebSocketService.subscribeOnEvents({
            eventType: "ACCOUNT_BLOCKED,CONVERSATION_ASSIGNED,CONVERSATION_REROUTING,CONVERSATION_MESSAGE,CONVERSATION_COMPLETED,CONVERSATION_ROUTED_OFF,CORPORATE_MESSAGE",
            fn: function (event) {
                console.log("ON EVENT: " + JSON.stringify(event));

                // если пользователя заблокировали - разлогинить его
                if (event.type == 'ACCOUNT_BLOCKED') {
                    $state.go("login", {params: JSON.stringify({reason: "Аккаунт заблокирован"})});
                    return;
                }

                var showNotification = true;
                if (event.type != 'CORPORATE_MESSAGE') {
                    var conversationDetails = event.details.split(":");
                    var eventDirection = conversationDetails[1];
                    var eventOwner = conversationDetails[2];

                    // не будем показывать уведомления для чужих обращений, на которые мы просто смотрим, и для событий, которые сгенерированы самим оператором
                    showNotification = (eventDirection == 'FROM_VISITOR' || eventDirection == 'NONE') && (eventOwner == '0' || eventOwner == that.accountData.user.id);
                    console.log("Show notification for details: " + conversationDetails + " is a " + showNotification);
                }

                if (showNotification) {
                    that.onShowNotification(event);

                    classifiersDAO.get().then(function (data) {
                        var idsByFilter = Restangular.stripRestangular(data);
                        that.messageCount = idsByFilter['NEW'].length + idsByFilter['NOT_ANSWERED'].length;

                        const play = (events, sound) => {
                            if (!!event && events.indexOf(event.type) != -1) sound.play();
                        };

                        // play(["CONVERSATION_ASSIGNED", "CONVERSATION_MESSAGE"], incomeMessage);
                        play(["CONVERSATION_ASSIGNED"], dialogStarted);
                        play(["CONVERSATION_MESSAGE"], incomeMessage);
                        play(["CONVERSATION_COMPLETED"], dialogFinished);
                        play(["CONVERSATION_REROUTING"], dialogFinished);
                        play(["CONVERSATION_OFFLINE", "OFFLINE"], dialogFinished);
                    }, function (error) {
                    });
                }
            }
        }, subscriptionId);

        that.viewMessages = function () {
            $state.go('conversations.online');
        };

        $scope.$on("$destroy", function handler() {
            WebSocketService.removeListener(subscriptionId);
        });

        // init count of new messages
        $scope.$on('initNewMessageCount', function (event, args) {
            that.newMessageCount = args.count;
        });
    }

    onShowNotification(event) {
        console.log("ON SHOW NOTIFICATION: " + JSON.stringify(event));
        var that = this;

        // сразу проинициализируем все флаги
        var showNotification = false;
        var showSnackbar = false;
        var linkToOpen = null;

        // для событий, связанных с обращениями - распарсим детали
        if (event.type != 'CORPORATE_MESSAGE') {
            var conversationDetails = event.details.split(":");
            var conversationId = conversationDetails[0];
            var isOnline = conversationDetails[3];

            // для эксперта и суперэксперта - не рассылать уведомления (за исключением CORPORATE_MESSAGE)
            if (that.accountData && that.accountData.user) {
                var userAccount = that.accountData.user.account;
                if (userAccount.role.name == 'EXPERT' || userAccount.role.name == 'SUPER_EXPERT') {
                    return;
                }
            }
            // нотификации будем показывать всегда, снекбар - реже
            showNotification = true;
            showSnackbar = true;

            // определим ссылку, которую будет содержать уведомление
            linkToOpen = isOnline ? "conversations.online.control" : "conversations.offline.control";
            if (event.type == 'CONVERSATION_REROUTING') {
                linkToOpen = "conversations.single";
            }
            var linkToCheck = isOnline ? 'conversations.online' : 'conversations.offline';
            var linkToCheckControl = linkToCheck + ".control";

            if (event.type == 'CONVERSATION_ASSIGNED' || event.type == 'CONVERSATION_MESSAGE') {
                // не показывать snackbar, если сейчас мы смотрим на страницу с данным обращением
                var selectedConversationId = that.accountService.getSelectedConversationId();
                showSnackbar = !selectedConversationId || selectedConversationId != conversationId;
                if (showSnackbar) {
                    // если сейчас открыта страница conversations.online для онлайн-обращений (или conversations.ofline для офлайн-обращений) - snackbar слать не надо
                    // если открыто какое-то
                    if (that.$state.current.name == linkToCheck || that.$state.current.name == linkToCheckControl) {
                        showSnackbar = false;
                    }
                }
            }
        }

        var text = null;
        var fn = null;
        var btnText = null;
        var href = null;
        let stateAndHandler = (stateName, stateData) => {
            return {
                state: {name: stateName, data: stateData},
                fn: () => that.$state.go(stateName, stateData),
                href: that.$state.href(stateName, stateData, {absolute: false})
            };
        };

        switch (event.type) {
            case "CONVERSATION_ASSIGNED":
                text = "Новое обращение";
                btnText = "Открыть";
                ({fn, href} = stateAndHandler(linkToOpen, {conversationId: conversationId}));
                break;

            case "CONVERSATION_ROUTED_OFF":
                text = "Обращение переведено в оффлайн";
                btnText = "Открыть";
                ({fn, href} = stateAndHandler(linkToOpen, {conversationId: conversationId}));
                break;

            case "CONVERSATION_REROUTING":
                text = "Обращение переведено на другого оператора";
                btnText = "Открыть";
                ({fn, href} = stateAndHandler(linkToOpen, {conversationId: conversationId}));
                break;

            case "CONVERSATION_MESSAGE":
                text = "Новое сообщение";
                btnText = "Открыть";
                ({fn, href} = stateAndHandler(linkToOpen, {conversationId: conversationId}));
                break;
            case "CORPORATE_MESSAGE":
                // init count of new messages
                console.log("Do not show corporate message");
                if (event.details.split(';')[1] == that.accountData.user.account.id || that.accountService.getSelectedCorporateChatId() == event.details.split(';')[0])
                    break;
                that.newMessageCount++;
                break;
            default:
                showNotification = false;
                showSnackbar = false;
                break;
        }

        if (showSnackbar) {
            console.log("SHOW SNACKBAR");
            this.SnackbarService.addSnack(text, fn, btnText);
        }

        if (showNotification) {
            console.log("SHOW NOTIFICATION");
            this.notificationManager.showNotification({
                type: event.type,
                title: text,
                href: "/workplace.html" + href
            }, false);
        }
    }

    // переход по менюшке сверху
    selectTopLevel(i) {
        this.menu.selectedIndex = i;
        this.menu.activeMenu = this.menus[i];
    };

    // переход по меню верхнего порядка слева
    // (которое появляется только на планшетах и телефонах)
    selectTopLevelLeft(i) {
        this.menu.selectedIndexLeft = i;
    };

    isTopLevelSelected(i) {
        return this.menu.selectedIndex === i;
    };

    isTopLevelLeftSelected(i) {
        return this.menu.selectedIndexLeft === i;
    };

    toggleSidebar() {
        this.menu.visibility = !this.menu.visibility;
    };

    /**
     *
     * @param parentIndex индекс родительского меню или null, если показываем submenu для active menu
     * @param index
     * @returns {boolean}
     */
    isSubmenuSelected(parentIndex, index) {
        return (parentIndex == null || this.isTopLevelSelected(parentIndex)) && this.menu.selectedSubmenuIndex === index;
    };

    // инициализировать меню, когда есть для этого данные
    // и назначить обработичики переключения пунктов
    initMenu() {
        var that = this;
        if (that.menus) {
            that.$rootScope.$on('$stateChangeSuccess',
                function (event, toState, toParams, fromState, fromParams) {
                    that.onStateChange();
                });
            this.menu.activeMenu = this.menus[this.menu.selectedIndex];
        }
        that.onStateChange();

        // init count of new messages
        that.$http.get("/account/corporate/init/" + that.accountService.getAccountData().user.account.id, {params: {}})
            .success(function (data) {
                let newMessageCount = 0;
                for (var i = 0; i < data.result.chats.length; i++) {
                    newMessageCount += data.result.chats[i].newMessageCount;
                }
                that.newMessageCount = newMessageCount;
            })
            .error(function (response) {
            });
    }

    onStateChange() {
        let that = this;
        if (that.menus) {
            let justTopLevel = false;
            for (let i = 0; i < that.menus.length; i++) {
                let obj = that.menus[i];
                if (that.$state.includes(obj.url)) {
                    if (that.$state.current.name == obj.url) {
                        justTopLevel = true;
                    }
                    that.selectTopLevel(i);
                    break;
                }
            }
            let inSubmenu = false;
            let activeMenu = that.menu.activeMenu;
            if (!activeMenu) {
                // если текущее не нашлось - выберем первое
                that.selectTopLevel(0);
                justTopLevel = true;
                activeMenu = that.menu.activeMenu;
            }
            if (activeMenu && activeMenu.submenu) {
                for (let i = 0; i < activeMenu.submenu.length; i++) {
                    let obj = activeMenu.submenu[i];
                    if (that.$state.includes(activeMenu.url + "." + obj.url)) {
                        inSubmenu = true;
                        that.menu.selectedSubmenuIndex = i;
                        that.menu.visibility = false;
                        break;
                    }
                }
            }

            if (!inSubmenu && justTopLevel) {
                // отправляемся на первый стейт в списке
                var defaultState = this.menu.activeMenu.url + "." + this.menu.activeMenu.submenu[0].url;
                this.$state.go(defaultState);
            }
        }
    }

    togglemenu() {
        this.menu.visibility = !this.menu.visibility;
    }

    showTimeoutStatusForm() {
        this.timeoutStatusForm.show = true;
    }

    hideTimeoutStatusForm() {
        this.timeoutStatusForm.data = {};
        this.timeoutStatusForm.show = false;
    }

    setStatus(statusName) {
        this.accountService.setStatus({status: statusName});
        this.hideTimeoutStatusForm();
    }

    setStatusWithTimeout(statusName) {
        this.accountService.setStatus({status: statusName, timeout: this.timeoutStatusForm.data.timeout});
        this.hideTimeoutStatusForm();
    }

    logout() {
        this.accountService.logout();
    }
}

MenuController.$inject = ["$scope", "$rootScope", "$state", "AccountService", "WebSocketService", "NotificationManager", "Restangular", "SnackbarService", "$http"];
