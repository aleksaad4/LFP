import {BaseFormController} from "../BaseFormController";
import urls from "../../urls";
import common from "../../common";

export class VABaseFormController extends BaseFormController {

    constructor(Restangular, $scope, $state, objId, formObj, formOptions, afterLoadCallback) {
        super($scope, $state);

        const that = this;

        // инициализируем форму
        this.initFormMode(formOptions);

        // форма редактирования ответа
        that.form.object = formObj;

        // контроллер списка
        that.listController = $scope.$parent.ctrl;
        that.restAngular = Restangular;

        // id объекта
        that.objId = objId;

        // поля, которые могут быть загружены
        that.attrs = [];
        that.tags = [];
        that.rules = [];
        that.tags = [];
        that.answers = [];
        that.sma = [];
        that.access = true;

        if (objId) {
            that.setModeEdit();
            that.setLoading(true);
            // по ID загружаем объект
            that.restAngular.one(that.listController.baseUrl, objId)
                .get()
                .then(function success(data) {
                    that.form.object = data;
                    if (afterLoadCallback != null) {
                        afterLoadCallback(that.form.object);
                    }
                }, function error(data) {

                })
                .finally(function () {
                    that.setLoading(false);
                });

        } else {
            // нет id - создание нового объекта
            that.setModeCreate();
            that.access = true;
        }
    }

    /**
     * Функция сохранения объекта
     * @param afterSendCallback callback после успешного создания/обновления - первый параметр isNew - был ли создан новый объект
     */
    commonSave(afterSendCallback) {
        const that = this;
        this.form.errors = [];

        // сохранить или обновить
        const saveFunc = that.mode.new ? that.restAngular.all(that.listController.baseUrl).post : that.restAngular.all(that.listController.baseUrl).patch;
        that.setLoading(true);

        saveFunc(this.form.object)
            .then(function success(data) {
                that.form.object = data;
                that.form.success = true;
                if (that.mode.new) {
                    that.setModeEdit();
                    // обновляем id объекта
                    that.objId = data.id;
                    //если новый объект, добавляем в список объектов в мастере
                    that.listController.addNewObj(that.form.object);
                    // создание
                    afterSendCallback(true, data);
                } else {
                    // заменяем в мастере объект, который редактировали
                    that.listController.replaceObj(that.form.object);
                    // обновление
                    afterSendCallback(false, data);
                }
            }, function error(data) {
                that.handleError(data);
            })
            .finally(function () {
                that.setLoading(false);
            });
    }

    /**
     * Функция удаления объекта
     * @param afterRemoveCallback callback после удаления объекта
     */
    commonRemove(afterRemoveCallback) {
        const that = this;
        that.setLoading(true);
        that.restAngular.one(that.listController.baseUrl, that.form.object.id).remove()
            .then(function success(data) {
                that.handleSuccess(data);
                that.listController.deleteObj(that.form.object);
                afterRemoveCallback();
            }, function error(data) {
                that.handleError(data);
            })
            .finally(function () {
                that.setLoading(false);
            });
    }

    /**
     * Функция получения доступа (по url: .../${id}/access) и сохранение его в поле .access
     */
    getAccess() {
        const that = this;
        that.restAngular.one(that.listController.baseUrl, that.objId).one("access").get()
            .then(function success(data) {
                that.access = data;
            }, function error(data) {
            });
    }

    /**
     * Загрузка связанных атрибутов (по url: .../${id}/attrs) и сохранение его в поле .attrs
     */
    loadAttributes() {
        return common.loadLinkedValues(this, this.listController.baseUrl, this.objId, "attrs");
    }

    /**
     * Загрузка dkb полей (по url: .../${id}/fields) и сохранение его в поле .fields
     */
    loadFields() {
        return common.loadLinkedValues(this, this.listController.baseUrl, this.objId, "fields");
    }

    /**
     * Загрузка связанных правил (по url: .../${id}/rules) и сохранение его в поле .rules
     */
    loadRules() {
        return common.loadLinkedValues(this, this.listController.baseUrl, this.objId, "rules");
    }

    /**
     * Загрузка связанных групп (по url: .../${id}/tags) и сохранение его в поле .tags
     */
    loadTags() {
        return common.loadLinkedValues(this, this.listController.baseUrl, this.objId, "tags");
    }

    /**
     * Загрузка связанных ответов (по url: .../${id}/answers) и сохранение его в поле .answers
     */
    loadAnswers() {
        return common.loadLinkedValues(this, this.listController.baseUrl, this.objId, "answers");
    }

    /**
     * Загрузка связанных стандартных ответов (по url: .../${id}/sma) и сохранение его в поле .sma
     */
    loadSMA() {
        return common.loadLinkedValues(this, this.listController.baseUrl, this.objId, "sma");
    }

    /**
     * Получение текущего аккаунта
     */
    loadAccount() {
        const that = this;
        that.restAngular.one(urls.va.account).one("current").get()
            .then(function success(data) {
                that.account = data;
                that.account.isJunior = function () {
                    return that.account.expertType == 'JUNIOR';
                };
            }, function error(data) {
            });
    }
}

VABaseFormController.$inject = ["Restangular", "$scope", "$state"];
