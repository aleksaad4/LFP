import {BaseFormController} from "./BaseFormController";

export class BaseCrudController extends BaseFormController {

    constructor(scope, state, stateParams, restAngular, stateName, extraStateParams) {
        super();

        const that = this;

        that.restAngular = restAngular;
        that.scope = scope;
        that.stateParams = stateParams;
        that.state = state;

        // id объекта
        that.objId = that.stateParams['id'];
        // название базового состояния (от которого дальше идёт .edit)
        that.stateName = stateName;
        // дополнительные параметры состояния, которые нужно указывать при изменении state
        that.extraStateParams = extraStateParams;

        // контроллер списка
        that.listController = that.scope.$parent.ctrl;

        if (that.objId) {
            // загружаем объект по id
            that.load();
        }
    }

    load() {
        const that = this;

        that.doAction(that.restAngular.one(that.listController.baseUrl, that.objId).get(),
            function success(data) {
                that.form.object = data;
                that.objLoadedCallback();
            });
    }

    objLoadedCallback() {
    }

    save(successClb) {
        const that = this;

        let create = that.objId == null || that.objId == 0 || that.objId == "";
        const saveFunc = create
            // сохранение
            ? that.restAngular.all(that.listController.baseUrl).post
            // редактирование
            : that.restAngular.all(that.listController.baseUrl).patch;

        // выполняем сохранение
        that.doActionS(saveFunc(that.form.object),
            function success(data) {
                that.form.object = data;
                if (create) {
                    that.objId = data.id;
                    that.listController.addNewObj(that.form.object);
                    let params = {id: data.id};
                    if (that.extraStateParams != null) {
                        params = $.extend(params, that.extraStateParams);
                    }
                    that.state.go(that.stateName + ".edit", params, {notify: false});
                } else {
                    that.listController.replaceObj(that.form.object);
                }
                if (successClb != null) {
                    successClb(create);
                }
            });
    }

    remove() {
        const that = this;

        // удаление объекта
        that.doAction(that.restAngular.one(that.listController.baseUrl, that.form.object.id).remove(),
            function success(data) {
                that.listController.deleteObj(that.form.object);
                let params = {};
                if (that.extraStateParams != null) {
                    params = $.extend(params, that.extraStateParams);
                }
                that.state.go(that.stateName, params, {reload: true});
            }
        )
    }

    loadValues(valuesName, successClb) {
        const that = this;

        that.doAction(that.restAngular.all(that.listController.baseUrl).all(valuesName).getList(),
            function (data) {
                that[valuesName] = data;
                if (successClb != null) {
                    successClb();
                }
            });
    }

    loadLinkedValues(valuesName, successClb) {
        const that = this;

        that.doAction(that.restAngular.one(that.listController.baseUrl, that.objId).all(valuesName).getList(),
            function (data) {
                that[valuesName] = data;
                if (successClb != null) {
                    successClb();
                }
            });
    }
}

BaseCrudController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
