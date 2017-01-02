function FileinputDirective() {
    return {
        restrict: 'AE',
        replace: true,
        scope: {
            'fileChange': '&',
            'previewUrl': '=',
            'accept': "@"
        },
        template: [
            '<div class="fileinput fileinput-new" data-provides="fileinput">',
            '<div class="fileinput-preview thumbnail" data-trigger="fileinput"></div>',
            '<div>',
            '<span class="btn btn-info btn-file">',
            '<span class="fileinput-new">Выберите файл</span>',
            '<span class="fileinput-exists">Изменить</span>',
            '<input name="fileinput" type="file">',
            '</span>',
            '<a href="#" class="m-l-5 btn btn-danger fileinput-exists" data-dismiss="fileinput">Удалить</a>',
            '</div>',
            '</div>'
        ].join(""),
        link: function (scope, element, attrs, ngModelCtrl) {
            const preview = element.find('.fileinput-preview');
            const input = element.find('input[type=file]');

            if (scope.previewUrl != null) {
                preview.html("<img src='" + scope.previewUrl + "'/>");
            }

            input.attr("accept", scope.accept);

            input.bind('change', function () {
                const me = this;
                scope.$apply(function () {
                    if (me.files.length > 0) {
                        scope.fileChange({item: me.files[0]});
                    } else {
                        scope.fileChange({item: null});
                    }
                });
            });

            scope.$watch('previewUrl', function () {
                if (scope.previewUrl != null) {
                    preview.html("<img src='" + scope.previewUrl + "'/>");
                } else {
                    preview.html("");
                }
            });

            element.fileinput();
        }
    };
}

export default FileinputDirective;