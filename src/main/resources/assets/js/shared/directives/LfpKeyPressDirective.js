function LfpKeyPressDirective() {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.bind("keypress", function (event) {
                const keyCode = event.which || event.keyCode;
                if (keyCode == attrs.lfpKeyCode) {
                    scope.$apply(function () {
                        scope.$eval(attrs.lfpKeyPress);
                    });
                }
            });
        }
    };
}

export default LfpKeyPressDirective;