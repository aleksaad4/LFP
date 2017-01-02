/**
 * Конфигурация путей для AngularJS
 */
import mainRoutes from "./routes/MainRoutes";
import accountsRoutes from "./routes/AccountsRoutes";
import footballRoutes from "./routes/FootballRoutes";

let allRoutes = [];
allRoutes = allRoutes.concat(mainRoutes);
allRoutes = allRoutes.concat(accountsRoutes);
allRoutes = allRoutes.concat(footballRoutes);
module.exports = allRoutes;