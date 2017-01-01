/**
 * Конфигурация путей для AngularJS
 */
import mainRoutes from "./routes/MainRoutes";
import accountsRoutes from "./routes/AccountsRoutes";

let allRoutes = [];
allRoutes = allRoutes.concat(mainRoutes);
allRoutes = allRoutes.concat(accountsRoutes);
module.exports = allRoutes;