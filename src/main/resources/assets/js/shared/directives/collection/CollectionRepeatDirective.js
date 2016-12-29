/**
 * Created by semyon on 22.06.16.
 */
function CollectionRepeatDirective(RepeatParser, $parse) {
    var pageable;
    var ctrl;
    return {
        restrict: "EA",
        require: "^collection",
        scope: "=",
        bindToController: true,
        controller: ["$scope", function ($scope) {
            ctrl = this;
        }],
        controllerAs: "$ctrlRepDir",
        compile: function (tElement, tAttrs) {
            var parent = tElement.parent();

            pageable = tAttrs["pageable"];
            if (!pageable) {
                pageable = tAttrs["data-pageable"];
            }

            var itemPerPage = tAttrs["itemPerPage"];
            if (!itemPerPage) {
                itemPerPage = tAttrs["data-item-per-page"];
            }
            if (!itemPerPage) {
                itemPerPage = 10;
            }

            if (!tAttrs.repeat) throw new Error('repeat', "Expected 'repeat' expression.");

            var parserResult = RepeatParser.parse(tAttrs.repeat);

            // достаём body этой директивы
            var children = tElement.children();

            // обернём детей в <div></div>
            var template = angular.element('<div></div>');
            var repeatExpression = parserResult.repeatExpression();

            // получаем левые и правые стороны выражения repeat
            const repExpSplit = repeatExpression.split(" in ");

            // назнвание item'a
            const itemName = repExpSplit[0];

            // всё справа от 'in' (название коллекции и фильтры)
            const repExpRightHandSplit = repExpSplit[1].split(" ");

            // название коллекции
            const collectionName = repExpRightHandSplit[0];

            // возможные фильтры, которые применяются к коллекции ещё до search, groupBy и paging
            let possibleFilters = '';

            // добавим всё что было справа от названия колле
            if (repExpRightHandSplit.length > 1) {
                for (var i = 1; i < repExpRightHandSplit.length; i++) {
                    var obj = repExpRightHandSplit[i];
                    possibleFilters += " " + obj;
                }
            }

            repeatExpression = itemName + " in $ctrlRepDir.$filtered = (" + collectionName + possibleFilters;

            var attrDirective = "ng-repeat";

            // включить поиск
            repeatExpression += " | filter: (collection.searchable ? collection.search : ''))";

            // включить сортировку
            repeatExpression += " | orderBy:collection.sortType:collection.sortReverse";
            // включить пейджинг
            if (pageable) {
                repeatExpression += " | limitTo : " + itemPerPage + " : (collection.curPage - 1) * " + itemPerPage;
            }

            repeatExpression += " as resultCollection";

            template.attr(attrDirective, repeatExpression);
            template.append(children);

            // Append this new template to our compile element
            tElement.html('');
            tElement.append(template);
            tElement.append("<div class='empty-collection' data-ng-hide='resultCollection.length'>Нет элементов</div>");

            return {
                pre: function (scope, element, attributes, controller) {
                },
                post: function (scope, element, attributes, collection) {
                    var items = collectionName;
                    collection.items = scope.$eval(items);
                    collection.itemPerPage = itemPerPage;
                    scope.$watchCollection(items, function (data) {
                        collection.items = data;
                    });

                    scope.collection = collection;
                    scope.collection.search = "";
                    scope.collection.pageable = pageable;
                    scope.collection.collectionRepeat = ctrl;
                }
            };
        }
    };

}

CollectionRepeatDirective.$inject = ["RepeatParser", "$parse"];

export default CollectionRepeatDirective;