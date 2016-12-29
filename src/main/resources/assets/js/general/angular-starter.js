import raven from "../general/raven";

exports.run = function (angularInitMethod) {

    raven.call(function () {
        const url = document.URL,
            standalone = !url || (url.indexOf("http://") === -1 && url.indexOf("https://") === -1),
            application = angularInitMethod(standalone),
            bootstrapAngular = function () {
                raven.call(function () {
                    angular.bootstrap(document, [application.name], {strictDi: true});
                });
            };

        if (standalone) {
            document.addEventListener("deviceready", bootstrapAngular(), false);
        } else {
            angular.element(document).ready(function () {
            // $(document).ready(function () {
                bootstrapAngular();
            });
        }
    });
};