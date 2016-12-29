/**
 * директива кнопки удаления объектов с подтверждением
 * Created by Olesya
 */
function DeleteButtonDirective() {
    return {
        restrict: 'EA',
        replace: true,
        scope: {
            confirmCallback: "&onDelete",
            buttonText: "@",
            questionText: "@",
            overlay: "<",
            // css класс для кнопок
            cssClass: "@",
            // объект, который передаётся в callback (как item)
            object: "="
        },
        templateUrl: "/pages/workplace/elements/deleteButton.html",
        controller: function () {
            var that = this;

            if (typeof that.overlay == 'undefined') {
                that.overlay = true;
            }

            var deleteCallback = that.confirmCallback;

            that.buttonTitle = this.buttonText ? this.buttonText : "Удалить";
            that.question = this.questionText ? this.questionText : "Вы уверены, что хотите удалить объект?";

            that.requestForDelete = function (item) {
                that.touched = true;
            };

            that.submitDelete = function () {
                deleteCallback({item: that.object});
                that.touched = false;
            };

            that.cancelDelete = function () {
                that.touched = false;
            };
        },
        bindToController: true,
        controllerAs: "dCtrl"
    };

}

export default DeleteButtonDirective;