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
    }
];