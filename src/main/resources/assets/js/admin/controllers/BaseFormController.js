export class BaseFormController {

    // в конструкторе создается объект формы
    constructor($scope, $state) {

        var that = this;
        that.$scope = $scope;
        that.$state = $state;
        that.form = {
            success: false,
            isLoading: false,
            errors: {}
        };

    }

    selectMode(mode) {
        var opts = this.modeOptions[mode];
        Object.keys(opts).forEach(function (key) {
            this.mode[key] = opts[key];
        }.bind(this));
    }

    /**
     * Выбрать режим редактирования существуюещего объекта
     * @optional options параметры, которые надо добавить/заменить в состоянии
     */
    setModeEdit(options) {
        var mode = "edit";
        if (options) {
            this.addModeOptions(mode, options);
        }
        this.selectMode(mode);
        this.mode.new = false;
    }

    /**
     * Выбрать режим редактирования просмотра
     * @optional options параметры, которые надо добавить/заменить в состоянии
     */
    setModeShow(options) {
        var mode = "show";
        if (options) {
            this.addModeOptions(mode, options);
        }
        this.selectMode(mode);
        this.mode.new = false;
    }

    isModeShow() {
        // todo: как проверить на текущий режим?
        return this.mode.title == this.modeOptions["show"].title;
    }

    /**
     * Выбрать режим создания нового объекта
     * @optional options параметры, которые надо добавить/заменить в состоянии
     */
    setModeCreate(options) {
        var mode = "create";
        if (options) {
            this.addModeOptions(mode, options);
        }
        this.selectMode(mode);
        this.mode.new = true;
    }

    initFormMode(options) {
        // по-умолчанию есть параметры для названия формы и текста в кнопке "сохранить"
        this.modeOptions = {
            "create": {
                title: "Новый объект",
                save: "Сохранить"
            },
            "edit": {
                title: "Редактирование",
                save: "Сохранить"
            },
            "show": {
                title: "Просмотр",
                save: "Редактировать"
            }
        };

        this.mode = {};

        if (!options) {
            return;
        }

        // берём ключи-состояния ("edit", "create")
        var optionsKeys = Object.keys(options);
        optionsKeys.forEach(function (mode) {
            // берём значения конкретного режима
            this.addModeOptions(mode, options[mode]);
        }.bind(this));
    }

    /***
     * Добавить или заменить параметры режима
     * e.g. addModeOptions("create", {title: "Перезаписываемый параметр", myNewVal: "Новый параметр"})
     * @param mode название режима
     * @param optionsForMode параметры
     */
    addModeOptions(mode, optionsForMode) {
        if (!this.modeOptions[mode]) {
            this.modeOptions[mode] = {};
        }
        Object.keys(optionsForMode).forEach(function (key) {
            this.modeOptions[mode][key] = optionsForMode[key];
        }.bind(this));
    }

    // успешная обработка
    handleSuccess(data) {
        // покажем, что загрузка закончена
        this.setLoading(false);
        // обнулим ошибки
        this.form.success = true;
        this.form.errors = {};
    };

    // отображение состояния загрузки
    setLoading(loading) {
        this.form.isLoading = loading;
    };

    handleError(data) {
        this.form.errors = {};
        // загрузка закончена
        this.setLoading(false);
        this.form.success = false;
        var errors = data.errors;

        // пришла 503, 502, 501, 500........ ошибка
        if (!errors) {
            if(data.status == 403){
                this.form.errors["#main"] = "Доступ запрещен";
            } else {
                this.form.errors["#main"] = "Внутренняя ошибка системы, повторите запрос позже";
            }
            return;
        }
        // список ошибок от сервера
        for (var i = 0; i < errors.length; i++) {
            var error = errors[i];
            var fieldKey = error.field;
            var msg = error.message;
            if (!fieldKey) {
                // ошибка без привязки к полю
                this.form.errors["#main"] = msg;
            } else {
                this.form.errors[fieldKey] = msg;
            }
        }
    }

}

BaseFormController.$inject = ["$scope", "$state"];