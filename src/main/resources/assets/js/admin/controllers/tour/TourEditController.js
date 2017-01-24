import {BaseCrudController} from "./../base/BaseCrudController";

export default class TourEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        // id турнира
        const tId = stateParams['tId'];

        super(scope, state, stateParams, restAngular, "tours", {tId: tId});

        const that = this;

        // в туре хранится ID турнира и список матчей
        that.form.object = {tournamentId: that.listController.tId, matchList: [], name: ""};

        that.matchForm = {};
        that.matchInitData = {teamAIsHome: true};

        // параметры для datepicker-а
        let moment = require("moment/js/moment");
        that.dateOptions = {
            minDate: moment(moment()).add(1, 'day')
        };
        that.dateFormat = "dd.MM.yyyy";

        // загрузка игроков
        that.loadValues("players", () => {
            // заполним map-у игроков по id
            that.id2player = super.getMapById(that.players);
        });

        // загрузка команд
        that.loadValues("teams", () => {
            // заполним map-у команд по id
            that.id2team = super.getMapById(that.teams);
        });

        // возможные статусы тура
        that.loadValues("statuses");

    }

    formatMatch(item) {
        return item.teamAIsHome ? (this.id2team[item.teamAId].name + " - " + this.id2team[item.teamBId].name)
            : (this.id2team[item.teamBId].name + " - " + this.id2team[item.teamAId].name);
    }

    validateMatch(item, list) {
        let errors = {hasErrors: true};
        if (item.data.teamAId == null) {
            errors.teamAId = "Небходимо выбрать команду А";
        }
        if (item.data.teamBId == null) {
            errors.teamBId = "Небходимо выбрать команду Б";
        }
        if (Object.keys(errors).length == 1) {
            errors.hasErrors = false;
        }

        return errors;
    }

    extendedSave() {
        const that = this;

        // todo: форматировать дату

        // вызываем сохранение объекта
        that.save();
    }

    openDatePicker(event) {
        event.preventDefault();
        event.stopPropagation();
        this.openedDatePicker = true;
    };
}

TourEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
