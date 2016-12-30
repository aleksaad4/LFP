(function() {
'use strict';

var module = angular.module('loopify.ui.numberPicker', [
    'loopify.ui.numberPicker.templates'
]);
module
    .directive('loopifyNumberPicker', ['numberPickerService', function (service) {
        'use strict';

        var config = {
                min: 0,
                max: Infinity,
                step: 1,
                enter: false,
                percent: false,
                label: undefined,
                methodRound: undefined
            },
            base = {
                restrict: 'E',
                scope: {
                    'value': '=',
                    'min': '@',
                    'max': '@',
                    'step': '@',
                    'enter': '@',
                    'percent': '@',
                    'label': '@',
                    'methodRound': '@'
                }
            };

        return angular.extend(base, {
            //check if number
            link: function (scope) {
                var opts = service.assignExtend(scope, config);
                if (!service.checkNumber([opts.min, opts.max, opts.step])) {
                    throw new Error('some value: (min, max or step) is not a valid number');
                }

                if (scope.percent) {
                    scope.percentLabel = '%';
                    scope.isPercent = true;
                }

                scope.id = service.getId();

                //transform string to number
                service.transform(opts);

                //change current value if min value is bigger
                if (opts.min > scope.value) {
                    scope.value = opts.min;
                }
                //change current value if max value is small
                if (opts.max < scope.value) {
                    scope.value = opts.max;
                }

                scope.incrementValue = function () {
                    if (scope.value >= (scope.isPercent ? 100 : opts.max)) {
                        return;
                    }
                    scope.value = +scope.value + opts.step;
                    scope.$broadcast('change');
                };
                scope.decrementValue = function () {
                    if (scope.value <= (scope.isPercent ? 0 : opts.min)) {
                        return;
                    }
                    scope.value = +scope.value - opts.step;
                    scope.$broadcast('change');
                };

                scope.togglePercentageValue = function () {
                    scope.isPercent = !scope.isPercent;
                    if (scope.isPercent) {
                        scope.percentLabel = '%';
                    } else {
                        scope.percentLabel = scope.label;
                    }
                };

                scope.$watch('percentLabel', function (newValue, oldValue) {
                    if (!newValue && !oldValue)
                        return false;
                    if (scope.isPercent) {
                        scope.value = scope.methodRound ? Math[scope.methodRound](scope.value / opts.max * 100) : scope.value / opts.max * 100;
                    } else {
                        scope.value = scope.methodRound ? Math[scope.methodRound](opts.max * scope.value / 100) : opts.max * scope.value / 100;
                    }
                });
            },
            templateUrl: 'templates/numberPicker.html'
        });
    }]);
module
    .directive('loopifyInputNumberPicker', ['numberPickerService', function (service) {
        return {
            link: function (scope, element) {
                var fn = function () {
                    var min = scope.isPercent ? 0 : +scope.min,
                        max = scope.isPercent ? 100 : +scope.max;

                    scope.canDown = scope.value > min;
                    scope.canUp = scope.value < max;
                    scope.isMaxValue = !scope.canUp;
                    scope.isMinValue = !scope.canDown;

                    if ((!service.checkNumber(scope.value) || scope.value > max || scope.value < min) && scope.value !== '') {
                        //set oldValue or min value if oldValue isn't number when newValue isn't a number or newValue more than max or newValue less than min
                        scope.value = service.checkNumber(scope.oldValue) ? scope.oldValue : scope.min;
                    }
                    scope.oldValue = scope.value;
                };
                element.on('change', function(){
                    fn();
                    scope.$apply();
                });
                scope.$on('change', fn);
                fn();
            }
        };
    }]);
module
    .service('numberPickerService', function () {
        'use strict';

        return {
            index: 0,
            assignExtend: function (dest, src) {
                var o = {};

                for (var key in src) {
                    if (dest[key]) {
                        o[key] = dest[key];
                    } else {
                        o[key] = src[key];
                        dest[key] = src[key];
                    }
                }
                return o;
            },
            isNumber: function (value) {
                var val = Number(value);
                return !isNaN(val) && val === +value;
            },
            toNumber: function (value) {
                return Number(value);
            },
            checkNumber: function (value) {
                var self = this,
                //count no numbers
                    cnn = 0;

                if (angular.isArray(value)) {
                    angular.forEach(value, function (v) {
                        if (!self.isNumber(v)) {
                            cnn += 1;
                        }
                    });
                    if (cnn > 0) {
                        return false;
                    }
                    return true;
                }
                if (!this.isNumber(value)) {
                    return false;
                }
                return true;
            },
            transform: function (opts) {
                for (var key in opts) {
                    opts[key] = this.toNumber(opts[key]);
                }
            },
            getId: function () {
                this.index += 1;
                return 'number-picker-' + this.index;
            }
        };
    });

})();