const FormControlMessagesComponent = {
    templateUrl: "/pages/shared/components/formControlMessages.html",
    bindings: {
        form: "<",
        controlId: "@",
        controlName: "@"
    },
    restrict: "EA",
    replace: true,
    transclude: true,
    controller: function () {
    }
};

export default FormControlMessagesComponent;