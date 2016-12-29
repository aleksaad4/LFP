export default class MainPageController {

    constructor($scope, $rootScope, $controller, $location) {
        $scope.word = "Hello from MainPageController";
    }
}

MainPageController.$inject = ["$scope", "$rootScope", "$controller", "$location"];
