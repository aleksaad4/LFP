/**
 * Created by semyon on 23.09.16.
 */

function ServerPageableCollection() {
    return {
        scope: {
            /**
             * @param successFunction
             * @param errorFunction
             */
            loadFunction: "&",
        },
        bindToController: true,
        replace: true,
        restrict: "E",
        controller: function () {

            var that = this;

            that.data = [];

            that.onPageSelected = function (pageNumber) {
                that.loadFunction(function (newData) {
                        that.data = [];
                        that.data.concat(newData);
                    },
                    function (error) {

                    });
            };

        },
        controllerAs: "$ctrl"
    };
}


ServerPageableCollection.$inject = [];
export default ServerPageableCollection;