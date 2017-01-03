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
    }
];
