import {BaseCrudController} from "./../base/BaseCrudController";

export default class TourEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular) {
        // id турнира
        const tId = stateParams['tId'];

        super(scope, state, stateParams, restAngular, "tours", {tId: tId});

        const that = this;

        // загрузка игроков
        that.loadValues("players", () => {
            // заполним map-у игроков по id
            let id2player = {};
            angular.forEach(that.players, function (p) {
                id2player[p.id] = p;
            });
            that.id2player = id2player;
        })
    }
}

TourEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
