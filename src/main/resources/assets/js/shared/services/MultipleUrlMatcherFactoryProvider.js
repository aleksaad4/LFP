function MultipleUrlMatcher(patterns, useCaseInsensitiveMatch, $urlMatcherFactoryProvider) {
    this.urlMatchers = [];
    this.params = [];
    this.values = {};

    Object.keys(patterns).forEach(function(locale) {
        var pattern = patterns[locale];
        var urlMatcher = $urlMatcherFactoryProvider.compile(pattern, useCaseInsensitiveMatch);
        this.urlMatchers.push(urlMatcher);
        this.params = urlMatcher.params;
    }.bind(this));
}

MultipleUrlMatcher.prototype.format = function (values) {
    this.values = angular.extend({}, values);
    return this.urlMatchers[0] ? this.urlMatchers[0].format(values) : null;
};

MultipleUrlMatcher.prototype.concat = function (pattern) {
    return this.urlMatchers[0] ? this.urlMatchers[0].concat(pattern) : null;
};

MultipleUrlMatcher.prototype.exec = function (path, searchParams) {
    var values = null;
    this.urlMatchers.some(function(matcher) {
        var urlMatcherValues = matcher.exec(path, searchParams);
        if (urlMatcherValues) {
            values = urlMatcherValues;
            return true;
        }
    }.bind(this));
    return values;
};

MultipleUrlMatcher.prototype.parameters = function () {
    return this.params;
};

/**
 * @ngdoc function
 * @name ui.router.util.type:MultipleUrlMatcher#validates
 * @methodOf ui.router.util.type:MultipleUrlMatcher
 *
 * @description
 * Checks an object hash of parameters to validate their correctness according to the parameter
 * types of this `UrlMatcher`.
 *
 * @param {Object} params The object hash of parameters to validate.
 * @returns {boolean} Returns `true` if `params` validates, otherwise `false`.
 */
MultipleUrlMatcher.prototype.validates = function (params) {
    return this.params.$$validates(params);
};

/*
 MultipleUrlMatcher.prototype.toString = function () {
 return this.source;
 };
 */

/**
 * @ngdoc object
 * @name ui.router.util.MultipleUrlMatcherFactory
 *
 * @description
 * Factory for {@link ui.router.util.type:MultipleUrlMatcher} instances. The factory is also available to providers
 * under the name `MultipleUrlMatcherFactoryProvider`.
 */
function MultipleUrlMatcherFactory($urlMatcherFactoryProvider) {

    var useCaseInsensitiveMatch = false;

    /**
     * @ngdoc function
     * @name ui.router.util.MultipleUrlMatcherFactory#caseInsensitiveMatch
     * @methodOf ui.router.util.MultipleUrlMatcherFactory
     *
     * @description
     * Define if url matching should be case sensistive, the default behavior, or not.
     *
     * @param {bool} value false to match URL in a case sensitive manner; otherwise true;
     */
    this.caseInsensitiveMatch = function(value){
        useCaseInsensitiveMatch = value;
    };

    /**
     * @ngdoc function
     * @name ui.router.util.MultipleUrlMatcherFactory#compile
     * @methodOf ui.router.util.MultipleUrlMatcherFactory
     *
     * @description
     * Creates a {@link ui.router.util.type:UrlMatcher} for the specified pattern.
     *
     * @param {string} pattern  The URL pattern.
     * @returns {ui.router.util.type:MultipleUrlMatcher}  The MultipleUrlMatcher.
     */
    this.compile = function (patterns) {
        return new MultipleUrlMatcher(patterns, useCaseInsensitiveMatch, $urlMatcherFactoryProvider);
    };

    /**
     * @ngdoc function
     * @name ui.router.util.MultipleUrlMatcherFactory#isMatcher
     * @methodOf ui.router.util.MultipleUrlMatcherFactory
     *
     * @description
     * Returns true if the specified object is a UrlMatcher, or false otherwise.
     *
     * @param {Object} object  The object to perform the type check against.
     * @returns {Boolean}  Returns `true` if the object has the following functions: `exec`, `format`, and `concat`.
     */
    this.isMatcher = function (o) {
        return isObject(o) && isFunction(o.exec) && isFunction(o.format) && isFunction(o.concat);
    };

    /* No need to document $get, since it returns this */
    this.$get = function () {
        return this;
    };
}

// Register as a provider so it's available to other providers
angular.module('ui.router.util').provider('MultipleUrlMatcherFactory', ['$urlMatcherFactoryProvider', MultipleUrlMatcherFactory]);
