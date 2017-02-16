import urls from "../../urls";
import {BaseListController} from "./../../../shared/controllers/base/BaseListController";

export default class AccountsController extends BaseListController {

    constructor(restAngular) {
        super(restAngular, urls.account);

        const that = this;

        that.header = [
            {title: "Логин", field: "login"},
            {title: "Имя", field: "name"}
        ];
    }
}

AccountsController.$inject = ["Restangular"];