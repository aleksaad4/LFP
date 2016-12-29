const Raven = require("raven-js/js/raven");

var raven = {
    ignoreErrors: [
        // Random plugins/extensions
        "top.GLOBALS",
        // See: http://blog.errorception.com/2012/03/tale-of-unfindable-js-error.html
        "originalCreateNotification",
        "canvas.contentDocument",
        "MyApp_RemoveAllHighlights",
        "http://tt.epicplay.com",
        "Can\'t find variable: ZiteReader",
        "jigsaw is not defined",
        "ComboSearch is not defined",
        "http://loading.retry.widdit.com/",
        "atomicFindClose",
        // Facebook borked
        "fb_xd_fragment",
        // ISP "optimizing" proxy - `Cache-Control: no-transform` seems to reduce this. (thanks @acdha)
        // See http://stackoverflow.com/questions/4113268/how-to-stop-javascript-injection-from-vodafone-proxy
        "bmi_SafeAddOnload",
        "EBCallBackMessageReceived",
        // See http://toolbar.conduit.com/Developer/HtmlAndGadget/Methods/JSInjection.aspx
        "conduitPage",
        // Generic error code from errors outside the security sandbox
        // You can delete this if using raven.js > 1.0, which ignores these automatically.
        "Script error."
    ],
    ignoreUrls: [
        // Facebook flakiness
        /graph\.facebook\.com/i,
        // Facebook blocked
        /connect\.facebook\.net\/en_US\/all\.js/i,
        // Woopra flakiness
        /eatdifferent\.com\.woopra-ns\.com/i,
        /static\.woopra\.com\/js\/woopra\.js/i,
        // Chrome extensions
        /extensions\//i,
        /^chrome:\/\//i,
        // Other plugins
        /127\.0\.0\.1:4001\/isrunning/i,  // Cacaoweb
        /webappstoolbarba\.texthelp\.com\//i,
        /metrics\.itunes\.apple\.com\.edgesuite\.net\//i
    ]
};

module.exports = {
    init: function (url, revision) {
        "use strict";

        if (NODE_ENV === "prod") {
            Raven.config(url, {
                release: revision,
                ignoreErrors: raven.ignoreErrors,
                ignoreUrls: raven.ignoreUrls,
                tags: {
                    git_commit: revision
                }
            }).install();

            console.log("Raven initialized");

            // this.addAjaxErrorHandling(); // todo: if jQuery

        } else {
            console.log("Raven not initialized");
        }
        return Raven;
    },

    call: function (func) {
        "use strict";

        try {
            func();
        } catch (e) {
            if (typeof Raven !== "undefined" && Raven.isSetup()) {
                Raven.captureException(e);
            }
            console.error(e);
        }
    },

    addAjaxErrorHandling: function () {
        "use strict";

        $(document).ajaxError(function (event, jqXHR, ajaxSettings, thrownError) {
            if (!!Raven && Raven.isSetup()) {
                Raven.captureMessage(thrownError || jqXHR.statusText, {
                    extra: {
                        type: ajaxSettings.type,
                        url: ajaxSettings.url,
                        data: ajaxSettings.data,
                        status: jqXHR.status,
                        error: thrownError || jqXHR.statusText,
                        response: jqXHR.responseText.substring(0, 100)
                    }
                });
            }
        });
    }
};