function ToggleSubMenuDirective() {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.click(function () {
                element.next().slideToggle(200);
                element.parent().toggleClass('toggled');
            });
        }
    };
}

export default ToggleSubMenuDirective;

