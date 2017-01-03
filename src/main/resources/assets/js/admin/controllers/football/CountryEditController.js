import {BaseCrudController} from "./../base/BaseCrudController";
import urls from "../../urls";

export default class CountryEditController extends BaseCrudController {

    constructor(scope, state, stateParams, restAngular, FileUploader) {
        super(scope, state, stateParams, restAngular, "football.countries");

        const that = this;
        that.fileUploader = new FileUploader({url: urls.fileupload.image});
    }

    choseImage(file) {
        this.image = file;
        this.wasEditImage = true;
    }

    resetImage() {
        this.image = null;
        this.wasEditImage = false;
    }

    extendedSave() {
        const that = this;

        // загружаем файл перед созданием/обновлением, если он был изменён
        if (that.image != null) {
            that.loadFile(that.fileUploader, that.image, "image", function (data) {
                // скидываем флаг о том, что было редактирование файла
                that.resetImage();
                // проставляем image url в объект
                that.form.object.imageUrl = data.url;
                // вызываем сохранение объекта
                that.save();
            });
        } else {
            if (that.wasEditImage) {
                // сбрасываем url на avatar
                that.form.object.imageUrl = null;
            }
            // вызываем сохранение объекта
            that.save();
        }
    }
}

CountryEditController.$inject = ["$scope", "$state", "$stateParams", "Restangular", "FileUploader"];
