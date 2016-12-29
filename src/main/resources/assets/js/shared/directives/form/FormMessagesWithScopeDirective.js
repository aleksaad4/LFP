function FormMessagesWithScopeDirective() {
    return {
        restrict: "EA",
        scope: {
            form: "<"
        },
        templateUrl: "/pages/shared/elements/formMessages.html",
        controller: function () {
        },
        bindToController: true,
        controllerAs: "ctrl"
    };
}

export default FormMessagesWithScopeDirective;