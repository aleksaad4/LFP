// конфигурация
import nprogesss from "./js/general/angular-nprogress";
import locationUI from "./js/general/angular-location-ui";
import routes from "./js/admin/routes";
import {TemplateCacheBuilder} from "./utils";
import "./js/shared/services/MultipleUrlMatcherFactoryProvider";
import "./less/skin-lfp.less";
import "./less/admin/admin";

/**
 * Инициализация Angular
 */
const initAngularAdminLfpApp = function () {
    "use strict";

    /**
     * Функция групповой инициализации данных
     * @param ngMethod конфигурациионный метод ангуляра, 1 параметр - имя модуля, 2 - функция/объект
     * @param components список компонент для инициализации
     */
    function populate(ngMethod, components) {
        components.keys().forEach(function (key) {
            populateItem(ngMethod, key, components(key));
        });
    }

    function populateItem(ngMethod, fileName, resource) {

        function capitalizeFirstLetter(string) {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }

        function lowercaseFirstLetter(string) {
            return string.charAt(0).toLowerCase() + string.slice(1);
        }

        const controllerName = fileName.replace(/.*\/(.*)\.js/g, "$1");
        const controller = resource.default;

        if (!!controller) {
            // var name = controller.name ? controller.name : controllerName;
            var name = controllerName;
            ["Directive", "Component"].forEach(function (burgerStylePostfix) {
                const rx = new RegExp("^(.+)" + burgerStylePostfix + "$");
                if (name.match(rx)) {
                    name = name
                        .replace(rx, "$1") // получаем основное название
                        .replace(/\.?([A-Z]+)/g, function (x, y) { // и преобразуем в dash-style
                            return capitalizeFirstLetter(y.toLowerCase())
                        });
                    name = lowercaseFirstLetter(name);
                }
            });

            ngMethod(name, controller);
        } else {
        }
    }

    /**
     * Групповая инициализация
     * @param app angular application
     * @param items загружаемые файлы
     */
    function module(app, items) {
        items.keys().forEach(function (fileName) {

            const resource = items(fileName);

            if (fileName.match(/.*Component\.js$/)) {
                populateItem(app.component, fileName, resource);
            } else if (fileName.match(/.*Controller\.js$/)) {
                populateItem(app.controller, fileName, resource);
            } else if (fileName.match(/.*Directive\.js$/)) {
                populateItem(app.directive, fileName, resource);
            } else if (fileName.match(/.*Service\.js$/)) {
                populateItem(app.service, fileName, resource);
            } else if (fileName.match(/.*Routes\.js$/)) {
                // ignore route - must be included manually
            } else {
            }
        });
    }

    const app = angular.module("adminLfpApp", [
        "ngRoute",
        "ngResource",
        "ngAnimate",
        "ngSanitize",
        "ngCookies",
        "ngMessages",
        "ngTagsInput",
        "ui.router",
        "ui.select",
        "ui.router.util",
        "ui.bootstrap",
        "ui.tree",
        "ntt.TreeDnD",
        "treeControl",
        "datatables",
        "angularFileUpload",
        "restangular",
        "daterangepicker",
        "checklist-model",
        "colorpicker.module",
        "loopify.ui.numberPicker",
        "ngFileSaver",
        "angularRandomString",
        "luegg.directives",
        "ui.bootstrap-slider",
        'nouislider',
        // 'ngTable'
    ]);

    // Инициазизация всех компонент (контролеров, директив etc) – package by layer
    populate(app.component, require.context("./js/admin/components/", true, /\.js$/));
    populate(app.controller, require.context("./js/admin/controllers/", true, /\.js$/));
    populate(app.directive, require.context("./js/admin/directives/", true, /\.js$/));
    populate(app.service, require.context("./js/admin/services/", true, /\.js$/));
    populate(app.filter, require.context("./js/admin/filters/", true, /\.js$/));

    //shared компоненты
    populate(app.controller, require.context("./js/shared/controllers/", true, /\.js$/));
    populate(app.component, require.context("./js/shared/components/", true, /\.js$/));
    populate(app.directive, require.context("./js/shared/directives/", true, /\.js$/));
    populate(app.service, require.context("./js/shared/services/", true, /\.js$/));
    populate(app.filter, require.context("./js/shared/filters/", true, /\.js$/));

    var Raven = require("raven-js/js/raven");
    Raven.addPlugin(require('raven-js/js/plugins/angular'));

    return app
        .config(nprogesss)
        .config(["$stateProvider", "MultipleUrlMatcherFactoryProvider", function ($stateProvider, MultipleUrlMatcherFactoryProvider) {
            for (var i = 0; i < routes.length; i++) {
                var rule = routes[i];

                if (Array.isArray(rule.options.url)) {
                    rule.options.url = MultipleUrlMatcherFactoryProvider.compile(rule.options.url);
                }

                $stateProvider.state(rule.state, rule.options)
            }
        }])

        .config(["$locationProvider", "$httpProvider", function ($locationProvider, $httpProvider) {
            $httpProvider.defaults.headers.common = {
                "X-Requested-With": "adminLfpApp"
            };

            // use the HTML5 History API
            $locationProvider.html5Mode(false);

        }])
        .config(["RestangularProvider", function (RestangularProvider) {
            RestangularProvider.addResponseInterceptor(function (data, operation, what, url, response, deferred) {
                if (data.status == "NOT_AUTHORIZED") {
                    location.reload();
                }
                if (data.status != "OK") {
                    deferred.reject(data);
                }
                return data;
            });
            RestangularProvider.setResponseExtractor(function (response, operation) {
                if (!response) {
                    return;
                }
                return response.result;
            });
            RestangularProvider.addErrorInterceptor(function (response) {
                if (response.status == 401) {
                    location.reload();
                }
            });
        }])
        .config(["$animateProvider", function ($animateProvider) {
            $animateProvider.classNameFilter(/sliding-item|fading-item|custom-anim-item/);
        }])

        .filter('formatLocation', function () {
            return function (location) {
                return location.displayTitle = location.country
                    + (location.region != null ? (", " + location.region) : "")
                    + (location.city != null ? (", " + location.city) : "");
            };
        })

        .filter('isEmpty', function () {
            var bar;
            return function (obj) {
                if (!obj) {
                    return true;
                }
                for (bar in obj) {
                    if (obj.hasOwnProperty(bar)) {
                        return false;
                    }
                }
                return true;
            };
        })

        .animation('.sliding-item', function () {
            return {
                enter: function (element, done) {
                    var el = jQuery(element);
                    // сначала hide, чтобы анимация не срабатывала сразу
                    el.hide().slideDown({complete: done});
                    return function (cancelled) {
                        if (cancelled === undefined) {
                            el.stop(true, true);
                            el.hide();
                        }
                    }
                },
                leave: function (element, done) {
                    var el = jQuery(element);
                    el.slideUp({complete: done});
                    return function (cancelled) {
                        if (cancelled === undefined) {
                            el.stop(true, true);
                            el.hide();
                        }
                    }
                }
            }
        })
        .animation('.fading-item', function () {
            return {
                enter: function (element, done) {
                    var el = jQuery(element);
                    // сначала hide, чтобы анимация не срабатывала сразу
                    el.hide().fadeIn({complete: done});
                    return function (cancelled) {
                        if (cancelled === undefined) {
                            el.stop(true, true);
                            el.hide();
                        }
                    }
                },
                leave: function (element, done) {
                    var el = jQuery(element);
                    el.fadeOut({complete: done});
                    return function (cancelled) {
                        if (cancelled === undefined) {
                            el.stop(true, true);
                            el.hide();
                        }
                    }
                }
            }
        })

        .run(locationUI)
        .run(TemplateCacheBuilder({
            "/pages/admin": require.context("./pages/admin/", true, /\.html$/),
            "/pages/shared": require.context("./pages/shared/", true, /\.html$/)
        }))
        .run(["$rootScope", "$location", function ($rootScope, $location) {
            $rootScope.testHeaders = $location.host() == "127.0.0.1";
        }]);
};

// запуск Angular
require("./js/general/angular-starter").run(initAngularAdminLfpApp);