function FgLineDirective() {
    return {
        restrict: 'C',
        link: function (scope, element) {
            if ($('.fg-line')[0]) {
                $('body').on('focus', '.form-control', function () {
                    $(this).closest('.fg-line').addClass('fg-toggled');
                });

                $('body').on('blur', '.form-control', function () {
                    const p = $(this).closest('.form-group');
                    const i = p.find('.form-control').val();

                    if (p.hasClass('fg-float')) {
                        if (i.length == 0) {
                            $(this).closest('.fg-line').removeClass('fg-toggled');
                        }
                    }
                    else {
                        $(this).closest('.fg-line').removeClass('fg-toggled');
                    }
                });
            }
        }
    };
}

export default FgLineDirective;