function BtnDirective() {
    return {
        restrict: 'C',
        link: function (scope, element) {

            const Waves = require("Waves/js/waves.min.js");

            if (element.hasClass('btn-icon') || element.hasClass('btn-float')) {
                Waves.attach(element, ['waves-circle']);
            }

            else if (element.hasClass('btn-light')) {
                Waves.attach(element, ['waves-light']);
            }

            else {
                Waves.attach(element);
            }

            Waves.init();
        }
    };
}

export default BtnDirective;