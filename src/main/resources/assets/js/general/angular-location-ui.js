export default function locationUI($rootScope, $location, $timeout) {

    // функция перехода по ссылке пути, добавляется задержка, отсутствующая при использвоании ng-touch
    $rootScope.location = function (path) {
        $timeout(function () {
            $location.path(path);
        }, 300);
    };

    $rootScope.isViewLoading = false;
    $rootScope.$on("$locationChangeStart", function () {
        $rootScope.isViewLoading = true;
    });
    $rootScope.$on("$routeChangeSuccess", function () {
        $rootScope.isViewLoading = false;
    });
}

locationUI.$inject = ["$rootScope", "$location", "$timeout"];
