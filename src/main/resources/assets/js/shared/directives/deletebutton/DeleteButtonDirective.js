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
        templateUrl: "/pages/shared/directives/deleteButton.html",
        controller: function () {
            const that = this;

            const deleteCallback = that.confirmCallback;

            // название кнопки
            that.button = this.buttonText ? this.buttonText : "Удалить";
            // вопрос
            that.question = this.questionText ? this.questionText : "Вы уверены, что хотите удалить объект?";

            // обработчик нажатия на кнопку удаления
            that.requestForDelete = function () {
                that.touched = true;
            };

            // подтверждение удаления
            that.submitDelete = function () {
                deleteCallback({item: that.object});
                that.touched = false;
            };

            // отмена удаления
            that.cancelDelete = function () {
                that.touched = false;
            };
        },
        bindToController: true,
        controllerAs: "dCtrl"
    };

}

export default DeleteButtonDirective;