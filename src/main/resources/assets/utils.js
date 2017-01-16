export function TemplateCacheBuilder(scanMap) {
    const templateUrls = {};
    for (let scanFolder in scanMap) {
        let scanResults = scanMap[scanFolder];
        scanResults.keys().forEach(function (key) {
            templateUrls[scanFolder + key.substring(1)] = scanResults(key);
        });
    }
    templateUrls["uib/template/datepicker/day.html"] = templateUrls["/pages/shared/components/datepicker/day.html"];
    templateUrls["uib/template/datepicker/month.html"] = templateUrls["/pages/shared/components/datepicker/month.html"];
    templateUrls["uib/template/datepicker/year.html"] = templateUrls["/pages/shared/components/datepicker/year.html"];
    templateUrls["uib/template/datepickerPopup/popup.html"] = templateUrls["/pages/shared/components/datepicker/popup.html"];
    return ['$templateCache', function ($templateCache) {
        for (let key in templateUrls) {
            $templateCache.put(key, templateUrls[key]);
        }
    }];
}
