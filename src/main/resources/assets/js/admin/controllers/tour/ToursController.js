import urls from "../../urls";
import {BaseListController} from "./../../../shared/controllers/base/BaseListController";

export default class ToursController extends BaseListController {

    constructor(restAngular, stateParams) {
        // id турнира
        const tId = stateParams['tId'];

        super(restAngular, urls.tour + "/" + tId);

        const that = this;

        // сохраним id турнира
        that.tId = tId;
        that.newTourState = "tours.edit({id:null, tId:" + that.tId + "})";

        that.header = [
            {title: "Название", field: "name"},
            {title: "ID", field: "id"},
            {title: "Статус", field: "status.title"}
        ];

        // загрузим турнир
        that.doAction(that.restAngular.one(that.baseUrl).one("tournament").get(),
            function (data) {
                that.tournament = data;
            });
    }
}

ToursController.$inject = ["Restangular", "$stateParams"];