module.exports = [
    {
        state: "section1",
        options: {
            url: "/section1",
            parent: "authorized"
        }
    },
    {
        state: "section1.section1_section2",
        options: {
            url: "/section2",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/section1/section2/example.html",
                    controller: "VaExampleController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "section1.section1_section2.detail"
            }
        }
    },
    {
        state: "section1.section1_section2.detail",
        options: {
            url: "/:id",
            views: {
                "details@section1.section1_section2": {
                    templateUrl: "/pages/admin/section1/section2/exampleDetail.html",
                    controller: "VaExampleDetailController",
                    controllerAs: "ctrl"
                }
            }
        }
    }
];
