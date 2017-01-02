function CollectionRepeatDirective(RepeatParser) {
    let pageable;
    let ctrl;

    function getVal(array, key, defaultValue) {
        return array[key] != null ? array[key] : defaultValue;
    }

    return {
        restrict: "EA",
        require: "^collection",
        scope: "=",
        bindToController: true,
        controller: ["$scope", function (scope) {
            ctrl = this;
        }],
        controllerAs: "collectionRepeat",
        compile: function (tElement, tAttrs) {
            // есть ли вообще пагинация?
            let pageable = getVal(tAttrs, "pageable", tAttrs["data-pageable"]);
            // сколько элементов на странице, по умолчанию 10
            let itemPerPage = getVal(tAttrs, "itemPerPage", getVal(tAttrs, "data-item-per-page", 10));

            // должна быть repeat дериктива
            if (!tAttrs.repeat) throw new Error('repeat', "Expected 'repeat' expression");

            // парсим repeat директиву
            let repeatParseResult = RepeatParser.parse(tAttrs.repeat);
            let repeatExpression = repeatParseResult.repeatExpression();
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
                for (let i = 1; i < repExpRightHandSplit.length; i++) {
                    let obj = repExpRightHandSplit[i];
                    possibleFilters += " " + obj;
                }
            }
            repeatExpression = itemName + " in collectionRepeat.$filtered = (" + collectionName + possibleFilters;

            let attrDirective = "ng-repeat";

            // включить поиск
            repeatExpression += " | filter: (collection.searchable ? collection.search : ''))";
            // включить сортировку
            repeatExpression += " | orderBy:collection.sortCurrentValue:collection.sortReverse";
            // включить пейджинг
            if (pageable) {
                repeatExpression += " | limitTo : " + itemPerPage + " : (collection.curPage - 1) * " + itemPerPage;
            }
            repeatExpression += " as resultCollection";

            // достаём body этой директивы
            let children = tElement.children();
            // обернём детей в <div></div>
            let template = angular.element('<div class="lv-item"></div>');
            template.attr(attrDirective, repeatExpression);
            template.append(children);

            // добавим наш элемент для отображения пустой коллекции
            tElement.html('');
            tElement.append(template);
            tElement.append("<div class='lv-item' data-ng-if='resultCollection.length == 0'><div class='lv-title'>Нет элементов</div></div>");

            return {
                pre: function (scope, element, attributes, controller) {
                },
                post: function (scope, element, attributes, collection) {
                    const items = collectionName;
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

CollectionRepeatDirective.$inject = ["RepeatParser"];

export default CollectionRepeatDirective;