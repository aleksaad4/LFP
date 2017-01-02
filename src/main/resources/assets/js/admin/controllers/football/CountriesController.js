import urls from "../../urls";
import {BaseListController} from "./../base/BaseListController";

export default class CountriesController extends BaseListController {

    constructor(restAngular) {
        super(restAngular, urls.football.country);

        const that = this;

        that.header = [
            {title: "Название", field: "name"}
        ];
    }
}

CountriesController.$inject = ["Restangular"];