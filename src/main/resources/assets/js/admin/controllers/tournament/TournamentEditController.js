import {BaseCrudController} from "./../base/BaseCrudController";

export default class TournamentEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        super(scope, state, stateParams, restAngular, "tournaments");

        const that = this;

        // страны
        that.loadValues("types");
    }

    canDelete() {
        // todo: удалять только с определенным статусом
        return this.form.object.id != null;
    }
}

TournamentEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
