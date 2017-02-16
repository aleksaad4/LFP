import {BaseCrudController} from "./../../../shared/controllers/base/BaseCrudController";
import urls from "../../urls";

export default class AccountEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular, FileUploader, accountService) {
        super(scope, state, stateParams, restAngular, "accounts");

        const that = this;
        that.accountService = accountService;
        that.fileUploader = new FileUploader({url: urls.fileupload.image});

        // типы аккаунтов
        that.loadValues("roles");
    }

    choseAvatar(file) {
        this.avatar = file;
        this.wasEditAvatar = true;
    }

    resetAvatar() {
        this.avatar = null;
        this.wasEditAvatar = false;
    }

    extendedSave() {
        const that = this;

        // загружаем файл перед созданием/обновлением, если он был изменён
        if (that.avatar != null) {
            that.loadFile(that.fileUploader, that.avatar, "avatar", function (data) {
                // скидываем флаг о том, что было редактирование файл
                that.resetAvatar();
                // проставляем avatar url в объект
                that.form.object.avatarUrl = data.url;
                // вызываем сохранение объекта
                that.save();
            });
        } else {
            if (that.wasEditAvatar) {
                // сбрасываем url на avatar
                that.form.object.avatarUrl = null;
            }
            // вызываем сохранение объекта
            that.save();
        }
    }
}

AccountEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular", "FileUploader", "AccountService"];
