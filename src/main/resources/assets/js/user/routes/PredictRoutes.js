module.exports = [
    {
        state: "predicts",
        options: {
            url: "/predicts",
            parent: "authorized",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/user/predict/predicts.html",
                    controller: "PredictsController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "predicts.edit"
            }
        }
    },
    {
        state: "predicts.edit",
        options: {
            url: "/:id",
            views: {
                "details@accounts": {
                    templateUrl: "/pages/user/predict/predictsEdit.html",
                    controller: "PredictsEditController",
                    controllerAs: "ctrl"
                }
            }
        }
    }
];
