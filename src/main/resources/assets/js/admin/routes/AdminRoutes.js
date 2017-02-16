module.exports = [
    {
        state: "authorized",
        options: {
            url: "/admin",
            views: {
                "main@": {
                    templateUrl: "/pages/shared/authorized.html"
                }
            }
        }
    }
];
