
import urls from "../../urls";
import {BaseListController} from "./../../../shared/controllers/base/BaseListController";

export default class LeaguesController extends BaseListController {

    constructor(restAngular) {
        super(restAngular, urls.football.league);

        const that = this;

        that.header = [
            {title: "Название", field: "name"},
            {title: "Страна", field: "country.name"}
        ];
    }
}

LeaguesController.$inject = ["Restangular"];