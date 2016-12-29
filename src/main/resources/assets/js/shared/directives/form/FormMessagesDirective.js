/**
 * директива сообщений формы - общих ошибок и успешного выполнения
 * Created by Olesya
 */
function FormMessagesDirective() {
    return {
        restrict: "EA",
        scope: "=",
        templateUrl: "/pages/shared/elements/formMessages.html"
    };
}

export default FormMessagesDirective;