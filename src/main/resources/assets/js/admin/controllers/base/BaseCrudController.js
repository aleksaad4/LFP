import {BaseFormController} from "./BaseFormController";

export class BaseCrudController extends BaseFormController {

    constructor(scope, state, stateParams, restAngular, stateName) {
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
            });
    }

    save() {
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
                    that.state.go(that.stateName + ".edit", {id: data.id}, {notify: false});
                } else {
                    that.listController.replaceObj(that.form.object);
                }
            });
    }

    remove() {
        const that = this;

        // удаление объекта
        that.doAction(that.restAngular.one(that.listController.baseUrl, that.form.object.id).remove(),
            function success(data) {
                that.listController.deleteObj(that.form.object);
                that.state.go(that.stateName, {reload: true});
            }
        )
    }

    loadValues(valuesName) {
        const that = this;

        that.doAction(that.restAngular.all(that.listController.baseUrl).all(valuesName).getList(),
            function (data) {
                that[valuesName] = data;
            });

    }
}

BaseCrudController.$inject = ["$scope", "$state", "$stateParams", "Restangular"];
