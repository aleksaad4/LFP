module.exports = [
    {
        state: "authorized",
        options: {
            url: "/user",
            views: {
                "main@": {
                    templateUrl: "/pages/shared/authorized.html"
                }
            }
        }
    }
];
