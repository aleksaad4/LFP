/**
 * Конфигурация путей для AngularJS
 */
import mainRoutes from "../shared/routes/MainRoutes";
import adminRoutes from "./routes/AdminRoutes";
import accountsRoutes from "./routes/AccountsRoutes";
import footballRoutes from "./routes/FootballRoutes";
import tournamentRoutes from "./routes/TournamentRoutes";

let allRoutes = [];
allRoutes = allRoutes.concat(mainRoutes);
allRoutes = allRoutes.concat(adminRoutes);
allRoutes = allRoutes.concat(accountsRoutes);
allRoutes = allRoutes.concat(footballRoutes);
allRoutes = allRoutes.concat(tournamentRoutes);
module.exports = allRoutes;