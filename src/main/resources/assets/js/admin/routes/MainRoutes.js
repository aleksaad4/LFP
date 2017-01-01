module.exports = [
    {
        state: "login",
        options: {
            url: ["/login?nextUrl&params", "/?nextUrl&params", "?nextUrl&params"],
            views: {
                "main@": {
                    templateUrl: "/pages/admin/auth/login.html",
                    controller: "LoginController",
                    controllerAs: "ctrl"
                }
            }
        }
    },
    {
        state: "authorized",
        options: {
            url: "/admin",
            views: {
                "main@": {
                    templateUrl: "/pages/admin/authorized.html"
                }
            }
        }
    },

    {
        state: "teams",
        options: {
            url: "/teams",
            parent: "authorized",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/account/accounts.html",
                    controller: "AccountsController",
                    controllerAs: "ctrl"
                }
            }
        }
    },
    {
        state: "teams.two",
        options: {
            url: "/two",
            parent: "teams",
        }
    },
    {
        state: "teams.one",
        options: {
            url: "/one",
            parent: "teams"
        }
    }
];