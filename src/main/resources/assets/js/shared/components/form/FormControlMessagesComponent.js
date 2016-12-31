const FormControlMessagesComponent = {
    templateUrl: "/pages/shared/components/form/formControlMessages.html",
    bindings: {
        form: "<",
        fieldName: "@",
        label: "@"
    },
    restrict: "EA",
    replace: true,
    transclude: true,
    controller: function () {
    }
};

export default FormControlMessagesComponent;