export function TemplateCacheBuilder(scanMap) {
    const templateUrls = {};
    for (let scanFolder in scanMap) {
        let scanResults = scanMap[scanFolder];
        scanResults.keys().forEach(function (key) {
            templateUrls[scanFolder + key.substring(1)] = scanResults(key);
        });
    }
    return ['$templateCache', function ($templateCache) {
        for (let key in templateUrls) {
            $templateCache.put(key, templateUrls[key]);
        }
    }];
}
