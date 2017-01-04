import {BaseCrudController} from "./../base/BaseCrudController";

export default class TournamentEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        super(scope, state, stateParams, restAngular, "tournaments");

        const that = this;

        // типы турниров
        that.loadValues("types");
        // игроки
        that.loadValues("players");
        // лиги для уже созданного турнира
        if (that.form.object != null) {
            that.loadLinkedValues("leagues");
        }
    }

    getPlayersLabel() {
        let pCount = (this.form.object == null || this.form.object.players == null) ? 0 : this.form.object.players.length;
        return "Участники [" + pCount + "]";
    }

    extendedSave() {
        const that = this;

        that.save(created => {
            // если это было создание турнира - то загружаем список лиг
            if (created) {
                that.loadLinkedValues("leagues");
            }
        });
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
        return this.form.object != null && this.form.object.status != null && (this.form.object.status.id + 1 == index);
    }

    isStepGreaterThanN(index) {
        return this.form.object != null && this.form.object.status != null && (this.form.object.status.id + 1 >= index);
    }

    finishNStep(index) {
        const that = this;

        // выполняем завершение очередного шага
        that.doActionS(that.restAngular.one(that.listController.baseUrl, that.form.object.id)
                .one("finish" + index + "Step").get(),
            function success(data) {
                that.form.object = data;
                that.listController.replaceObj(that.form.object);
            });
    }
}

TournamentEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
