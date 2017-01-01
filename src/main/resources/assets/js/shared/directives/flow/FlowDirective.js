function FlowDirective(rootScope, state) {
    return {
        restrict: 'EA',
        controller: ["$scope", function ($scope) {
            const that = this;

            function isDetails() {
                if (state.current.hasOwnProperty("data") && state.current.data.hasOwnProperty("details")) {
                    return false;
                } else {
                    if (state.current.data && state.current.data.details) {
                        return true;
                    }
                }
                return false;
            }

            function checkState(event, toState, toParams, fromState, fromParams) {
                if (state.current.hasOwnProperty("data") && state.current.data.hasOwnProperty("details")) {
                    // перешли на мастера, значит надо показать master
                    that.showMaster();
                } else {
                    // перешли на details
                    if (state.current.data && state.current.data.details) {
                        that.showDetail();
                    }
                }
            }

            let removeStateChangeSuccessListener = rootScope.$on('stateChangeSuccess', checkState);

            $scope.$on("$destroy", function destroyListener() {
                removeStateChangeSuccessListener();
            });


            // показать рабочую область слева (мастер)
            this.showMaster = function () {
                if (isDetails()) {
                    state.go(state.$current.parent);
                }
                this.masterVisible = true;
            };

            // показать рабочую область справа (детейлс)
            this.showDetail = function () {
                this.masterVisible = false;
                window.scrollTo(0, 0);
            };

            checkState();
        }],
        bindToController: true,
        controllerAs: "flow",
        compile: function (tElement, tAttrs) {
            const children = tElement.children();

            let master = null;
            let detail = null;

            if (children.length == 2) {
                master = tElement.find("*[master]");
                if (!master) {
                    throw new Error("No 'master' element inside Flow");
                }

                detail = tElement.find("*[detail]");
                if (!detail) {
                    throw new Error("No 'detail' element inside Flow");
                }
            }

            let screenSize = tElement.attr("screen-size");
            if (!screenSize) {
                //по-умолчанию для телефонов и планшетов
                screenSize = ["sm", "xs"];
            } else {
                screenSize = screenSize.split(",");
            }

            for (let i = 0; i < screenSize.length; i++) {
                const obj = screenSize[i];
                screenSize[i] = "hidden-" + obj;
            }
            let clsString = screenSize.join(" ");

            master.attr("ng-class", "{'" + clsString + "': !flow.masterVisible}");
            detail.attr("ng-class", "{'" + clsString + "': flow.masterVisible}");

            const template = angular.element('<div></div>');
            template.append(children);

            // добавляем скомпилированных детей к элементу директивы
            tElement.html('');
            tElement.append(template);

            return function link(scope, element, attrs, flow) {
                scope.flow = flow;
            };
        }
    };
}

FlowDirective.$inject = ["$rootScope", "$state"];

export default FlowDirective;