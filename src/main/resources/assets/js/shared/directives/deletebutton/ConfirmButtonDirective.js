function ConfirmButtonDirective() {
    return {
        restrict: 'EA',
        scope: {
            confirmCallback: "&onConfirm",
            buttonText: "@",
            questionText: "@",
            overlay: "<",
            // css класс для кнопок
            cssClass: "@",
            mainBtnCssClass: "@",
            // объект, который передаётся в callback (как item)
            object: "="
        },
        templateUrl: "/pages/shared/directives/confirmButton.html",
        controller: function () {
            const that = this;

            const cc = that.confirmCallback;

            // название кнопки
            that.button = this.buttonText ? this.buttonText : "Удалить";
            // вопрос
            that.question = this.questionText ? this.questionText : "Вы уверены, что хотите удалить объект?";
            // css класс для главной кнопки
            that.btnCssClass = this.mainBtnCssClass ? this.mainBtnCssClass : "btn-danger pull-right";

            // обработчик нажатия на кнопку
            that.requestForConfirm = function () {
                that.touched = true;
            };

            // подтверждение
            that.submitConfirm = function () {
                cc({item: that.object});
                that.touched = false;
            };

            // отмена
            that.cancelConfirm = function () {
                that.touched = false;
            };
        },
        bindToController: true,
        controllerAs: "dCtrl"
    };

}

export default ConfirmButtonDirective;