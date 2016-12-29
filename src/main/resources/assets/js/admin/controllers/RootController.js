import moment from "moment/js/moment";
import "moment/js/locale/ru";

let webSocketService;

export default class RootController {

    constructor($scope, $rootScope, $controller, $location, $state, $timeout, Restangular, AccountService, WebSocketService, TabService, snackbarService, uibModal) {
        this.$rootScope = $rootScope;
        this.$scope = $scope;
        this.$state = $state;
        this.accountService = AccountService;
        this.tabService = TabService;

        this.snackbarService = snackbarService;
        this.uibModal = uibModal;

        webSocketService = WebSocketService;

        this.user = null;
        this.$timeout = $timeout;
        var that = this;
        that.restAngular = Restangular;

        //показываем прогрессбар
        that.isLoading = true;

        // слушаем события перехода на стейт
        //.on() возвращает функцию, которую можно вызвать чтобы удалить созданные listener
        var removeListenerFunction = $rootScope.$on("$stateChangeStart", function (event, newState, newStateParams) {

            // первый переход на стейт вообще в приложении
            // проверим авторизацию и перейдём либо на логин
            // либо на старый стейт

            // Отменяем ивент, т.к. проверка действия асинхронная
            event.preventDefault();
            that.isAuthorized(function (isAuthorized) {
                //скрываем прогрессбар
                that.isLoading = false;
                if (isAuthorized) {

                    if (newState.name.startsWith("login")) {
                        // переходим на первую вкладку первой страницы
                        $state.transitionTo(that.menu[0].url + "." + that.menu[0].submenu[0].url);
                    } else {
                        // переходим на изначальный state
                        $state.transitionTo(newState,
                            newStateParams, {reload: true, inherit: false, notify: true});
                    }
                } else {

                    if (newState.name.startsWith("login")) {
                        $state.go("login", newStateParams);
                    } else {
                        $state.go("login", {nextUrl: newState.name, params: JSON.stringify(newStateParams)});
                    }
                }
            });
            // слушатель должен отработать только один раз, удаляем его
            removeListenerFunction();

            // установка русской локали для moment
            moment.locale("ru");

            moment.calendarFormat = function (myMoment, now) {
                const diff = myMoment.diff(now, 'days', true);
                const weekDiff = myMoment.week() - now.week();
                var retVal = '';

                if (diff < -6) {
                    retVal = 'sameElse';
                } else if (diff < -1) {
                    if (weekDiff < 0) {
                        retVal = 'sameElse';
                    } else {
                        retVal = 'lastWeek';
                    }
                } else if (diff < 0) {
                    retVal = 'lastDay';
                } else if (diff < 1) {
                    retVal = 'sameDay';
                } else {
                    retVal = 'sameElse';
                }
                return retVal;
            };

        });

        that.lastUserActionTime = new Date().getTime();

        document.onclick = function () {
            that.lastUserActionTime = new Date().getTime();
        };
        document.onmousemove = function () {
            that.lastUserActionTime = new Date().getTime();
        };
        document.onkeypress = function () {
            that.lastUserActionTime = new Date().getTime();
        };

        // получить данные о пользовательских действиях от другой вкладки
        that.listenUserAction = function(event){
            var timestamp = event.data.timestamp;
            if(timestamp > that.lastUserActionTime){
                that.lastUserActionTime = timestamp;
            }
        };
        // отправить данные о пользовательских действиях в другие вкладки
        that.sendUserAction = function(){
            that.tabService.broadcast("uamonitor", {timestamp: that.lastUserActionTime});
        };

        // будем слушать события от других вкладок
        that.tabService.registerBroadcastHandler("uamonitor", that.listenUserAction);

        // отслеживаем логины/логауты в других вкладках
        that.listenLoginLogout = function(event){
            console.log("Listen event: " + JSON.stringify(event) + ", reload page");
            location.reload();
        };
        that.tabService.registerBroadcastHandler("login", that.listenLoginLogout);
        that.tabService.registerBroadcastHandler("logout", that.listenLoginLogout);

        that.refreshStatusFunc = function () {
            // проверить, были ли за последнее время движения мышки/клавиатуры, если нет - показать подтверждение,
            // если подтверждение нажато - отправить запрос на refresh статуса
            // если движения мышки-клавы были - просто отправить refresh

            let accData = that.accountService.data;

            let timeout = 60 * 1000;
            if (accData && accData.statusData.statusTimeout) {
                // пусть таймаут будет в два раза чаще, чем на сервере
                timeout = accData.statusData.statusTimeout / 2;
            }
            if (new Date().getTime() - that.lastUserActionTime >= timeout) {
                that.showConfirmPresenceDialog();
            } else {
                that.accountService.refreshStatus();
                that.sendUserAction();
            }

            that.$timeout(that.refreshStatusFunc, timeout);
        };

    }

    showConfirmPresenceDialog() {
        let that = this;
        const promise = that.$timeout(function () {
            that.accountService.logout();
        }, 60 * 1000);
        const cancelTimeout = () => that.$timeout.cancel(promise);
        let modalInstance = this.uibModal.open(
            {
                controller: 'PresenceConfirmationDialogController',
                controllerAs: "$ctrl",
                templateUrl: '/pages/workplace/modal/presenceConfirmation.html',
                openedClass: 'blur-main modal-open',
                windowClass: 'presence-confirmation modal-primary'
            }
        );
        modalInstance.result.then(function () {
            cancelTimeout();
            that.accountService.refreshStatus();
        }, function () {
            cancelTimeout();
            // dismiss то же самое что клик
            that.accountService.refreshStatus();
        });

    }

    /**
     * Проверяем авторизацию (TODO: настоящая проверка авторизации)
     * @param callback
     */
    isAuthorized(callback) {
        var that = this;
        // обращаемся на сервер, чтобы проверить, есть ли в сессии авторизационные данные
        that.restAngular.one("/account/login").get().then(function success(data) {
            // отправить главному контроллеру сообщение об авторизации
            that.onAuthorization(data);
            callback(true);
        }, function error(data) {
            callback(false);
        });
    }

    onAuthorization(data) {
        console.log("Authorization");
        var that = this;
        that.menu = data.menu;
        that.accountService.setUser(data.user);
        that.accountService.setNestedDepartments(data.nestedDepartments);
        that.$timeout(that.refreshStatusFunc, 60 * 1000);

        //webSocketService.connect(data.user);
        webSocketService.setIds(data.user.id, null, true);
    }
}

RootController.$inject = ["$scope", "$rootScope", "$controller", "$location",
    "$state", "$timeout", "Restangular", "AccountService", "WebSocketService", "TabService", "SnackbarService", "$uibModal"];
