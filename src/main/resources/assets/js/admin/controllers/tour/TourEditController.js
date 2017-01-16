import {BaseCrudController} from "./../base/BaseCrudController";

export default class TourEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        // id турнира
        const tId = stateParams['tId'];

        super(scope, state, stateParams, restAngular, "tours", {tId: tId});

        const that = this;

        that.matchForm = {};
        that.matchInitData = {teamAIsHome: true};
        that.dateOptions = {
            minDate: new Date(),
        };
        that.dateFormat = "dd.MM.yyyy";

        // загрузка игроков
        that.loadValues("players", () => {
            // заполним map-у игроков по id
            let id2player = {};
            angular.forEach(that.players, function (p) {
                id2player[p.id] = p;
            });
            that.id2player = id2player;
        });

        // загрузка команд
        that.loadValues("teams");
    }

    formatMatch(item) {
        return item.name;
    }

    validateMatch(item, list) {
        return {hasErrors: false};
    }

    openDatePicker(event) {
        event.preventDefault();
        event.stopPropagation();
        this.openedDatePicker = true;
    };
}

TourEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
