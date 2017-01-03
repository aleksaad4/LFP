import urls from "../../urls";
import {BaseListController} from "./../base/BaseListController";

export default class TeamsController extends BaseListController {

    constructor(restAngular) {
        super(restAngular, urls.football.team);

        const that = this;

        that.header = [
            {title: "Название", field: "name"},
            {title: "Город", field: "city"},
            {title: "Страна", field: "country.name"}
        ];
    }
}

TeamsController.$inject = ["Restangular"];