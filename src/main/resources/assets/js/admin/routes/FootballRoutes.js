module.exports = [
    {
        state: "football",
        options: {
            url: "/football",
            parent: "authorized",
        }
    },
    {
        state: "football.countries",
        options: {
            url: "/countries",
            parent: "football",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/football/countries.html",
                    controller: "CountriesController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "football.countries.edit"
            }
        }
    },
    {
        state: "football.countries.edit",
        options: {
            url: "/:id",
            views: {
                "details@football.countries": {
                    templateUrl: "/pages/admin/football/countryEdit.html",
                    controller: "CountryEditController",
                    controllerAs: "ctrl"
                }
            }
        }
    },
    {
        state: "football.leagues",
        options: {
            url: "/leagues",
            parent: "football",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/football/leagues.html",
                    controller: "LeaguesController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "football.leagues.edit"
            }
        }
    },
    {
        state: "football.leagues.edit",
        options: {
            url: "/:id",
            views: {
                "details@football.leagues": {
                    templateUrl: "/pages/admin/football/leagueEdit.html",
                    controller: "LeagueEditController",
                    controllerAs: "ctrl"
                }
            }
        }
    },
    {
        state: "football.teams",
        options: {
            url: "/teams",
            parent: "football",
            views: {
                "content@authorized": {
                    templateUrl: "/pages/admin/football/teams.html",
                    controller: "TeamsController",
                    controllerAs: "ctrl"
                }
            },
            data: {
                details: "football.teams.edit"
            }
        }
    },
    {
        state: "football.teams.edit",
        options: {
            url: "/:id",
            views: {
                "details@football.teams": {
                    templateUrl: "/pages/admin/football/teamEdit.html",
                    controller: "TeamEditController",
                    controllerAs: "ctrl"
                }
            }
        }
    },
];
