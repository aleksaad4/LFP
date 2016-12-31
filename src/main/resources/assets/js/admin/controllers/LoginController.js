import urls from "../urls";
import {BaseFormController} from "./BaseFormController";


export default class LoginController extends BaseFormController {

    constructor(scope, state, stateParams, Restangular) {
        super();
        const that = this;

        that.scope = scope;
        that.state = state;
        that.stateParams = stateParams;

        that.loginRest = Restangular.all(urls.login);
        that.form.object = {};
    }

    login() {
        const that = this;

        // отправляем форму
        that.doAction(that.loginRest.post(that.form.object),
            function success(data) {
                // root controller
                const rootController = that.scope.rCtrl;
                // пробросим в root контроллер данные полученные после авторизации
                rootController.handleAuthorizeData(data);

                let nextState = that.stateParams["nextUrl"];
                if (nextState == null) {
                    // на вкладку по умолчанию
                    that.$state.go(rootController.getDefaultState());
                } else {
                    // на запрашиваемую вкладку
                    let params = that.stateParams["params"];
                    that.$state.go(nextState, params == null ? null : JSON.parse(params));
                }
            });
    }
}

LoginController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];