function ToggleSidebarDirective() {
    return {
        restrict: 'A',
        scope: {
            sideMenu: '='
        },
        link: function (scope, element) {
            element.on('click', function () {
                if (scope.sideMenu == false) {
                    scope.$apply(function () {
                        scope.sideMenu = true;
                    })
                }
                else {
                    scope.$apply(function () {
                        scope.sideMenu = false;
                    })
                }
            })
        }
    };
}

export default ToggleSidebarDirective;