export class BaseFormController {

    constructor() {
        const that = this;

        // создаём переменную для формы
        that.form = {
            success: false,
            isLoading: false,
            errors: {}
        };
    }

    /**
     * Выполнение операции по загрузке чего-либо
     * @param action загрузочный action
     * @param success success callback, may be null
     * @param error error callback, may be null
     */
    doAction(action, success, error) {
        const that = this;

        // начинаем загрузку
        that.setLoading(true);
        action.then(function (data) {
            // успешно завершаем загрузку
            that.handleSuccess();
            // вызываем success-callback
            if (success != null) {
                success(data);
            }
        }, function (data) {
            // завершаем загрузку не успешно
            that.handleError(data);
            // вызываем error-callback
            if (error != null) {
                error(data);
            }
        });
    }

    /**
     * Успешное выполнение операции
     */
    handleSuccess() {
        // загрузка закончена
        this.setLoading(false);
        // обнуляем ошибки
        this.form.success = true;
        this.form.errors = {};
    };


    /**
     * Неуспешное выполнение операции
     * @param data
     */
    handleError(data) {
        // загрузка закончена
        this.setLoading(false);
        // обнуляем ошибки
        this.form.success = false;
        this.form.errors = {};

        const errors = data.errors;

        // вообще не получили массив ошибок, пришла ошибка с сервера с каким-то кодом
        if (!errors) {
            if (data.status == 403) {
                // forbidden
                this.form.errors["#main"] = "Доступ запрещен";
            } else {
                // internal server error
                this.form.errors["#main"] = "Внутренняя ошибка системы, повторите запрос позже";
            }
            return;
        }

        // получили список ошибок от сервера
        for (let i = 0; i < errors.length; i++) {
            const error = errors[i];
            let fieldKey = error.field;
            const msg = error.message;

            if (!fieldKey) {
                // ошибка без привязки к полю
                this.form.errors["#main"] = msg;
            } else {
                // ошибка с привязкой к полю
                this.form.errors[fieldKey] = msg;
            }
        }
    }

    /**
     * Установка состояния загрузки
     * @param loading идёт ли загрузка
     */
    setLoading(loading) {
        this.form.isLoading = loading;
    };
}