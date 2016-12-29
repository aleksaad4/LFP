/**
 * директива к полям формы - подписи, обозначение ошибок
 * Created by Olesya
 */
function FormControlMessagesDirective() {
    return {
        restrict: "EA",
        replace: true,
        transclude: true,
        scope: {
            form: "=",
            controlId: "@",
            controlName: "@"
        },
        templateUrl: "/pages/shared/elements/formControlMessages.html",
        controller: function () {
        },
        bindToController: true,
        controllerAs: "fCtrl"
    };
}

export default FormControlMessagesDirective;