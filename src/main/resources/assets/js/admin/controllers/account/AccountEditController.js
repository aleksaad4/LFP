import {BaseCrudController} from "./../base/BaseCrudController";

export default class AccountEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        super(scope, state, stateParams, restAngular);

        const that = this;

        // типы аккаунтов
        that.loadValues("roles");
    }
}

AccountEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
