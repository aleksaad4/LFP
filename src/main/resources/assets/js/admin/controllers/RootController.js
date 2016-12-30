import moment from "moment/js/moment";
import "moment/js/locale/ru";

let webSocketService;

export default class RootController {

    constructor(scope, rootScope, controller, location, state, timeout, restAngular) {
        const that = this;

        that.rootScope = rootScope;
        that.scope = scope;
        that.state = state;
        that.timeout = timeout;
        that.restAngular = restAngular;

        that.user = null;

        // показываем прогрессбар
        that.isLoading = true;

        // слушаем события перехода на стейт
        //.on() возвращает функцию, которую можно вызвать чтобы удалить созданные listener
        const removeListenerFunction = that.rootScope.$on("$stateChangeStart", function (event, newState, newStateParams) {

            // первый переход на стейт вообще в приложении
            // проверим авторизацию и перейдём либо на логин
            // либо на старый стейт

            // Отменяем ивент, т.к. проверка действия асинхронная
            event.preventDefault();

            // проверяем авторизацию
            that.isAuthorized(function (isAuthorized) {
                // скрываем прогрессбар
                that.isLoading = false;

                if (isAuthorized) {
                    if (state == null || state.name.startsWith("login")) {
                        // переходим на первую страницу интерфейса - первую страницу первой вкладки
                        that.state.transitionTo(that.getDefaultState());
                    } else {
                        // переходим на state, который запрашивали перед авторизацией
                        that.state.transitionTo(newState, newStateParams, {reload: true, inherit: false, notify: true});
                    }
                } else {
                    if (newState.name.startsWith("login")) {
                        // запрашиваемая страница - логин, так что запоминать нечего
                        that.state.go("login", newStateParams);
                    } else {
                        // запоминаем запрашиваемую страницу в параметры, чтобы после авторизации перейти к ней
                        that.state.go("login", {nextUrl: newState.name, params: JSON.stringify(newStateParams)});
                    }
                }
            });

            // слушатель должен отработать только один раз, удаляем его
            removeListenerFunction();
        });

        // настройка moment
        RootController.momentSetup();

        // detect Mobile Browser - add ismobile class
        if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
            angular.element('html').addClass('ismobile');
        }
    }

    /**
     * @returns {string} state по умолчанию
     */
    getDefaultState() {
        return this.menu[0].url + "." + this.menu[0].submenu[0].url;
    }

    /**
     * Настройка локали и других параметров moment
     */
    static momentSetup() {
        moment.locale("ru");
        moment.calendarFormat = function (myMoment, now) {
            const diff = myMoment.diff(now, 'days', true);
            const weekDiff = myMoment.week() - now.week();
            let retVal = '';

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
    }

    /**
     * Функция для проверки есть ли авторизационные данные в сессии
     * @param callback с параметром true - успешная авторизация, иначе нет
     */
    isAuthorized(callback) {
        const that = this;
        // обращаемся на сервер, чтобы проверить, есть ли в сессии авторизационные данные
        that.restAngular.one("/account/login").get().then(function success(data) {
            // отправить главному контроллеру сообщение об авторизации
            that.handleAuthorizeData(data);
            callback(true);
        }, function error(data) {
            // нет авторизации
            callback(false);
        });
    }

    /**
     * Функция сохранения данных полученных после выполнения авторизации
     * @param data данные после проверки авторизации
     */
    handleAuthorizeData(data) {
        // запоминаем меню
        this.menu = data.menu;

        // todo: запоминаем admin-а
    }
}

RootController.$inject = ["$scope", "$rootScope", "$controller", "$location", "$state", "$timeout", "Restangular"];
