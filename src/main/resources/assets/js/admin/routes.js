/**
 * Конфигурация путей для AngularJS
 */
import mainRoutes from "./routes/MainRoutes";
import accountsRoutes from "./routes/AccountsRoutes";
import footballRoutes from "./routes/FootballRoutes";
import tournamentRoutes from "./routes/TournamentRoutes";

let allRoutes = [];
allRoutes = allRoutes.concat(mainRoutes);
allRoutes = allRoutes.concat(accountsRoutes);
allRoutes = allRoutes.concat(footballRoutes);
allRoutes = allRoutes.concat(tournamentRoutes);
module.exports = allRoutes;