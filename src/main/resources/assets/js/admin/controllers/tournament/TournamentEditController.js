import {BaseCrudController} from "./../base/BaseCrudController";

export default class TournamentEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        super(scope, state, stateParams, restAngular, "tournaments");

        const that = this;

        // типы турниров
        that.loadValues("types");
        // игроки
        that.loadValues("players");
    }

    /**
     * Загрузка дополнительной информации
     */
    objLoadedCallback() {
        const that = this;
        // если сейчас второй шаг - для выбора лиги и количества туров
        if (that.isNStep(2)) {
            // загружаем лиги
            that.loadLinkedValues("leagues");
            // загружаем количества туров и кругов
            that.loadLinkedValues("tourAndRoundCounts");
        }
    }

    extendedSave() {
        const that = this;

        that.save(() => {
            // загружаем доп. информацию
            that.objLoadedCallback();
        });
    }

    getPlayersLabel() {
        let pCount = (this.form.object == null || this.form.object.players == null) ? 0 : this.form.object.players.length;
        return "Участники [" + pCount + "]";
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
                // по завершении очередного шага загружаем необходимую дополнительную информацию
                that.objLoadedCallback()
            });
    }
}

TournamentEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
