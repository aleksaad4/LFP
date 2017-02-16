import urls from "../../urls";
import {BaseListController} from "./../../../shared/controllers/base/BaseListController";

export default class PredictsController extends BaseListController {

    constructor(restAngular) {
        super(restAngular, urls.predict);

        const that = this;

        that.header = [];
    }
}

PredictsController.$inject = ["Restangular"];