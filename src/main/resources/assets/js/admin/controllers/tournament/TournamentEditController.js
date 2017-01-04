import {BaseCrudController} from "./../base/BaseCrudController";

export default class TournamentEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        super(scope, state, stateParams, restAngular, "tournaments");

        const that = this;

        // типы турниров
        that.loadValues("types");
        // игроки
        that.loadValues("players");
        // лиги
        that.loadValues("leagues");
    }

    /**
     * Можно ли удалить турнир?
     */
    canDelete() {
        // удалить можно только существующий турнир
        // который находится на этапе настройки
        return this.form.object != null && this.form.object.id != null
            && this.form.object.status.isConfiguration;
    }

    isNStep(index) {
        return this.form.object != null && (this.form.object.status.id + 1 == index);
    }

    isStepGreaterThanN(index) {
        return this.form.object != null && (this.form.object.status.id + 1 >= index);
    }

    finishFirstStep() {
        const that = this;

        // выполняем завершение первого шага
        that.doActionS(that.restAngular.one(that.listController.baseUrl, that.form.object.id)
                .customGET("finishFirstStep"),
            function success(data) {
                that.form.object = data;
            });
    }
}

TournamentEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
