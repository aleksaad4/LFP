import urls from "../../urls";
import {BaseListController} from "./../../../shared/controllers/base/BaseListController";

export default class TournamentsController extends BaseListController {

    constructor(restAngular) {
        super(restAngular, urls.tournament);

        const that = this;

        that.header = [
            {title: "Название", field: "name"},
            {title: "Статус", field: "status.title"},
            {title: "Тип", field: "type.title"}
        ];
    }
}

TournamentsController.$inject = ["Restangular"];