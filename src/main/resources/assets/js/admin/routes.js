/**
 * Конфигурация путей для AngularJS
 */

import mainRoutes from "./routes/MainRoutes";
import exampleRoutes from "./routes/ExampleRoutes";

let allRoutes = [];
allRoutes = allRoutes.concat(mainRoutes);
// allRoutes = allRoutes.concat(exampleRoutes);
module.exports = allRoutes;