module.exports = [
    {
        state: "login",
        options: {
            url: ["/login?nextUrl&params", "/?nextUrl&params", "?nextUrl&params"],
            views: {
                "main@": {
                    templateUrl: "/pages/admin/auth/loginForm.html",
                    controller: "LoginController",
                    controllerAs: "lCtrl"
                }
            }
        }
    },
    {
        state: "authorized",
        options: {
            url: "/workplace",
            views: {
                "main@": {
                    templateUrl: "/pages/admin/authorized.html"
                }
            }
        }
    }
];