
import {BaseFormController} from "./BaseFormController";

// контроллер для логина
export default class LoginController extends BaseFormController {

    constructor($scope, $rootScope, $state, $stateParams, Restangular, TabService) {
        super($scope, $state);
        this.tabService = TabService;
        this.loginRest = Restangular.all("/account/login");
        this.$stateParams = $stateParams;

        var initialStateParamsString = this.$stateParams["params"];
        var initialStateParams = {};
        if (initialStateParamsString) {
            initialStateParams = JSON.parse(initialStateParamsString);
            this.reason = initialStateParams.reason;
        }
    }

    login() {
        var that = this;

        // будем отображать что мы сейчас что-то грузим
        that.setLoading(true);
        // пробуем запостить данные логина
        that.loginRest.post(that.form)
            .then(function success(data) {
                that.handleSuccess(that, data);
                // рассылаем событие логина
                that.tabService.broadcast("login", data);

                var parentController = that.$scope.rCtrl;
                // отправить главному контроллеру сообщение об авторизации
                parentController.onAuthorization(data);

                // по умолчанию переходим к первому пункту меню
                var initialState = that.$stateParams["nextUrl"];
                var initialStateParamsString = that.$stateParams["params"];
                var initialStateParams = {};
                if (initialStateParamsString) {
                    initialStateParams = JSON.parse(initialStateParamsString);
                }

                if (!initialState || initialState.startsWith("login")) {
                    initialState = parentController.menu[0].url + "." + parentController.menu[0].submenu[0].url;
                }
                that.$state.go(initialState, initialStateParams);
            }, function error(data) {
                that.handleError(data);
            });
    }
}
// логин контроллер получает профиль пользователя и меню
LoginController.$inject = ["$scope", "$rootScope", "$state", "$stateParams", "Restangular", "TabService"];