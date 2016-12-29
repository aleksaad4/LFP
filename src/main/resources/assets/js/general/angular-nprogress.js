/**
 * Отображение процесса загрузки асинхронных запросов
 */

// импорт JS и стилей
//import NProgress from "nprogress"; // bower_components

// external
import "nprogress/css/nprogress";
import NProgress from "nprogress/js/nprogress";

export default function nprogesss($httpProvider, $provide) {
    NProgress.configure({
        showSpinner: false
    });
    $provide.factory("NProgressHttpInterceptor", ["$q", function ($q) {
        var requestCounter = 0;
        return {
            // On request success
            request: function (config) {
                if (requestCounter++ === 0) {
                    NProgress.start();
                }
                return config || $q.when(config);
            },

            // On request failure
            requestError: function (rejection) {
                return $q.reject(rejection);
            },

            // On response success
            response: function (response) {
                if (--requestCounter === 0) {
                    NProgress.done();
                }
                // Return the response or promise.
                return response || $q.when(response);
            },

            // On response failture
            responseError: function (rejection) {
                if (--requestCounter === 0) {
                    NProgress.done();
                }
                // Return the promise rejection.
                return $q.reject(rejection);
            }
        };
    }]);

    // Add the interceptor to the $httpProvider.
    $httpProvider.interceptors.push("NProgressHttpInterceptor");
}

// магия di
nprogesss.$inject = ["$httpProvider", "$provide"];