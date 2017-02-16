function EditableListDirective() {
    return {
        restrict: 'E',
        replace: true,
        transclude: {
            'formBody': 'div'
        },
        templateUrl: "/pages/shared/directives/editableList.html",
        bindToController: true,
        controllerAs: "elCtrl",
        scope: {
            // название объектов в списке
            itemTypeTitle: "@",
            // коллекция item-ов
            list: "=",
            // объект формы
            form: "=",
            initData: "=",
            formatFunc: "&",
            validateFunc: "&",
            canDelete: "=",
            canEdit: "=",
            canAdd: "="
        },
        controller: function () {
            const that = this;

            that.showForm = function () {
                that.form.show = true;
                that.form.errors = {hasErrors: false};
            };

            that.hideForm = function () {
                that.form.show = false;
            };

            that.addItem = function () {
                that.form.data = angular.copy(that.initData);
                that.form.new = true;
                that.form.idx = null;
                that.showForm();
            };

            that.editItem = function (idx) {
                that.form.data = angular.copy(that.list[idx]);
                that.form.new = false;
                that.form.idx = idx;
                that.showForm();
            };

            that.saveItem = function () {
                that.form.errors = that.validateFunc != null ? that.validateFunc({
                        item: that.form,
                        list: that.list
                    }) : {hasErrors: false};
                if (!that.form.errors.hasErrors) {
                    if (that.form.new) {
                        that.list.push(that.form.data);
                    } else {
                        that.list[that.form.idx] = that.form.data;
                    }
                    that.hideForm();
                }
            };

            that.deleteItem = function (idx) {
                that.hideForm();
                that.list.splice(idx, 1);
            };

            that.moveUp = function (idx) {
                if (idx > 0) {
                    that.hideForm();
                    const prev = that.list[idx - 1];
                    that.list[idx - 1] = that.list[idx];
                    that.list[idx] = prev;
                }
            };

            that.moveDown = function (idx) {
                if (idx < that.list.length - 1) {
                    that.hideForm();
                    const prev = that.list[idx + 1];
                    that.list[idx + 1] = that.list[idx];
                    that.list[idx] = prev;
                }
            }

        }
    }
}

export default EditableListDirective;