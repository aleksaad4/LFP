module.exports = [
    {
        state: "tournaments",
        options: {
            url: "/tournaments",
            parent: "authorized",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/tournament/tournaments.html",
                    controller: "TournamentsController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "tournaments.edit"
            }
        }
    },
    {
        state: "tournaments.edit",
        options: {
            url: "/:id",
            views: {
                "details@tournaments": {
                    templateUrl: "/pages/admin/tournament/tournamentEdit.html",
                    controller: "TournamentEditController",
                    controllerAs: "ctrl"
                }
            }
        }
    },
    {
        state: "tours",
        options: {
            url: "/:tId/tours",
            parent: "authorized",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/tour/tours.html",
                    controller: "ToursController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "tours.edit"
            }
        }
    },
    {
        state: "tours.edit",
        options: {
            url: "/:id",
            views: {
                "details@tours": {
                    templateUrl: "/pages/admin/tour/tourEdit.html",
                    controller: "TourEditController",
                    controllerAs: "ctrl"
                }
            }
        }
    }
];
