function CollectionDirective() {
    return {
        restrict: "EA",
        replace: true,
        transclude: {
            'body': 'collectionBody',
        },
        scope: {
            sortHeader: "=", // ассоциативный массив с полями title и field для сортировки
            sortDefaultValue: "@", // дефолтное значение для сортировки

            searchable: "=", // показывать ли поле ввода для поиска

            title: "=", // заголовок блока

            showLoading: "=", // нужно ли показывать процесс загрузки

            actionable: "=", // нужно ли показывать action-кнопку
            actionClick: "&", // действие при клике на action-кнопку
            actionLink: "@" // ссылка на action-кнопке
        },
        templateUrl: "/pages/shared/directives/collection/collection.html",
        bindToController: true,
        controllerAs: "collection",
        controller: function () {
            const that = this;

            // есть ли сортировка - зависит не только от флага, но и от задания sortHeader-а (если он отсуствует сортировки не будет)
            that.sortable = that.sortHeader != null;
            // текущее поле для сортировки
            that.sortCurrentValue = that.sortDefaultValue;
            // направление сортировки
            that.sortReverse = false;

            // обработчик клика на кнопку с полем для сортировки
            that.selectSortType = function (type) {
                // выбираем поле
                that.sortCurrentValue = type;
                // делаем reverse сортировки
                that.toggleSortReverse();
            };

            // изменение порядка сортировки
            that.toggleSortReverse = function () {
                that.sortReverse = !that.sortReverse;
            };

            that.btnActionClick = function () {
                that.actionClick();
            };

            // первая выбранная страница
            that.currentPage = 1;
        },
        compile: function (tElement, tAttr) {
            // Q: почему не ng-class?
            // A: потому что ng-class ещё не успевает добавить sliding-item когда ng-if выполняет enter()-функцию
            const collapsible = tAttr["collapsible"];
            if (!collapsible) {
                $(tElement).find(".sliding-item").removeClass("sliding-item");
            }
            return function (scope, element, attrs, ctrl, $transclude) {
                $transclude(scope, function (clone) {
                });
            };
        }
    };
}

export default CollectionDirective;