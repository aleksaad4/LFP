/**
 * Конфигурация путей для AngularJS
 */
import mainRoutes from "../shared/routes/MainRoutes";
import userRoutes from "./routes/UserRoutes";
import predictRoutes from "./routes/PredictRoutes";

let allRoutes = [];
allRoutes = allRoutes.concat(mainRoutes);
allRoutes = allRoutes.concat(userRoutes);
allRoutes = allRoutes.concat(predictRoutes);
module.exports = allRoutes;