module.exports = [
    {
        state: "accounts",
        options: {
            url: "/accounts",
            parent: "authorized",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/account/accounts.html",
                    controller: "AccountsController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "accounts.edit"
            }
        }
    },
    {
        state: "accounts.edit",
        options: {
            url: "/:id",
            views: {
                "details@accounts": {
                    templateUrl: "/pages/admin/account/accountEdit.html",
                    controller: "AccountEditController",
                    controllerAs: "ctrl"
                }
            }
        }
    },
];
