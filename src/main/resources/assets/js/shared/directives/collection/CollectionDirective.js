/**
 * Created by semyon on 22.06.16.
 */

function CollectionDirective() {
    return {
        restrict: "EA",
        replace: true,
        transclude: {
            'body': 'collectionBody',
            'header': '?collectionHeader'
        },
        scope: {
            header: "=",
            sortable: "=", // нужно ли показывать хедер с сортировкой
            defaultValue: "@",
            paginationId: "@",
            searchable: "=",
            title: "=",
            oneCard: "=",
            withoutCard: "=",
            collapsible: "=",
            showLoading: "="
        },
        bindToController: true,
        templateUrl: "/pages/workplace/collection.html",
        controller: function () {
            var defaultValue = this.defaultValue;
            this.sortType = defaultValue != null ? defaultValue : null;

            // нужно создать отдельное поле в объекте контроллера,, иначе поле будет перезаписано дефолтным объектом из parent scope
            var isSortable = (this.sortable != null && this.sortable !== "") ? this.sortable : true;
            if (!this.header) {
                isSortable = false;
            }

            // sortable по-дефолту
            this.isSortable = isSortable;
            this.sortReverse = false;

            // если можно коллапсить, то по умолчанию скрываем
            this.isVisible = this.collapsible ? false : true;

            this.selectSortType = function (type) {
                this.sortType = type;
                this.toggleSortReverse();
            };

            this.toggleSortReverse = function () {
                this.sortReverse = !this.sortReverse;
            };

            this.toggleVisibility = function () {
                if (!this.collapsible) {
                    return;
                }
                this.isVisible = !this.isVisible;
            };

            this.curPage = 1;

        },
        controllerAs: "collection",
        compile: function (tElement, tAttr) {
            // Q: почему не ng-class?
            // A: потому что ng-class ещё не успевает добавить sliding-item когда ng-if выполняет enter()-функцию
            var collapsible = tAttr["collapsible"];
            if (!collapsible) {
                $(tElement).find(".sliding-item").removeClass("sliding-item");
            }
            return function (scope, element, attrs,ctrl, $transclude) {
                $transclude(scope, function (clone) {
                });
            };
        }
    };
}

export default CollectionDirective;