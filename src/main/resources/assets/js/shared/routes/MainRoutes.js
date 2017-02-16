module.exports = [
    {
        state: "login",
        options: {
            url: ["/login?nextUrl&params", "/?nextUrl&params", "?nextUrl&params"],
            views: {
                "main@": {
                    templateUrl: "/pages/shared/auth/login.html",
                    controller: "LoginController",
                    controllerAs: "ctrl"
                }
            }
        }
    }
];