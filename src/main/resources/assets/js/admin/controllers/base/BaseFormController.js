export class BaseFormController {

    constructor() {
        const that = this;

        // создаём переменную для формы
        that.form = {
            success: false,
            isLoading: false,
            errors: {},
        };
    }

    doActionS(action, success, error) {
        this.doAction(action, success, error, true);
    }

    /**
     * Выполнение операции по загрузке файла
     * @param fileUploader загрузчик файла
     * @param file файл для загрузки
     * @param fieldName название поля для отображения ошибок возле него (если не задано - то #main)
     * @param success success callback, may be null
     * @param error error callback, may be null
     */
    loadFile(fileUploader, file, fieldName, success, error) {
        const that = this;

        // добавляем файл в очередь
        fileUploader.addToQueue(file);

        // обработчик ошибки
        fileUploader.onErrorItem = function (item, response, status, headers) {
            // завершаем загрузку не успешно
            that.handleError(response, fieldName);
            that.fileUploader.clearQueue();
        };

        // обработчик успешной загрузки
        fileUploader.onSuccessItem = function (item, response, status, headers) {
            // тут нужно проверить статус ответа от сервера
            if (response.status == 'OK') {
                // успешно завершаем загрузку
                that.handleSuccess(false);
                that.fileUploader.clearQueue();
                // вызываем success-callback
                if (success != null) {
                    success(response.result);
                }
            } else {
                // завершаем загрузку не успешно
                that.handleError(response, fieldName);
                that.fileUploader.clearQueue();
                // вызываем error-callback
                if (error != null) {
                    error(response.result);
                }
            }
        };

        // начинаем загрузку
        that.startLoading();
        // загружаем файл
        fileUploader.uploadItem(0);
    }

    doAction(action, successClb, errorClb, setSuccessFlag) {
        const that = this;

        // начинаем загрузку
        that.startLoading();
        action.then(function success(data) {
            // успешно завершаем загрузку
            that.handleSuccess(setSuccessFlag);
            // вызываем success-callback
            if (successClb != null) {
                successClb(data);
            }
        }, function error(data) {
            // завершаем загрузку не успешно
            that.handleError(data);
            // вызываем error-callback
            if (errorClb != null) {
                errorClb(data);
            }
        });
    }

    /**
     * Успешное выполнение операции
     */
    handleSuccess(setSuccessFlag) {
        // если статус операции не задан, то значит это первая загрузка
        // в таком случае проставляем успешный статус
        if (this.form.success == null) {
            // если нужно переключать success флаг, то ставим его в true, иначе проставляем null
            this.form.success = setSuccessFlag ? true : null;
        }

        // загрузка завершена
        this.finishLoading();
    };


    /**
     * Неуспешное выполнение операции
     * @param data
     */
    handleError(data, defaultKey) {
        // неуспех операции
        this.form.success = false;
        // вытаскиваем ошибки
        const errors = this.extractErrors(data, defaultKey);
        // добавляем ошибки в массив
        $.extend(this.form.errors, errors);

        // загрузка завершена
        this.finishLoading();
    }

    extractErrors(data, defaultKey) {
        const errors = data.errors;

        const result = {};

        // вообще не получили массив ошибок, пришла ошибка с сервера с каким-то кодом
        if (!errors) {
            if (data.status == 403) {
                // forbidden
                this.form.errors[defaultKey == null ? "#main" : defaultKey] = "Доступ запрещен";
            } else {
                // internal server error
                this.form.errors[defaultKey == null ? "#main" : defaultKey] = "Внутренняя ошибка системы, повторите запрос позже";
            }
            return;
        }

        // список ошибок от сервера
        for (let i = 0; i < errors.length; i++) {
            const error = errors[i];
            const fieldKey = error.field;
            const msg = error.message;
            if (!fieldKey) {
                // ошибка без привязки к полю
                result[defaultKey == null ? "#main" : defaultKey] = msg;
            } else {
                result[fieldKey] = msg;
            }
        }
        return result;
    }


    /**
     * Обработка окончания загрузки
     */
    finishLoading() {
        // уменьшаем счётчик количества загрузок
        this.form.loadingCounter = (this.form.loadingCounter != null ? this.form.loadingCounter : 0) - 1;
        // все загрузки завершились
        if (this.form.loadingCounter == 0) {
            this.setLoading(false);
        }
    }

    /**
     * Обработка начала загрузки
     */
    startLoading() {
        // если это первый запрос - то скидываем список ошибок в пусто
        if (this.form.loadingCounter == null || this.form.loadingCounter == 0) {
            this.form.errors = {};
            this.form.success = null;
        }
        // увеличиваем счётчик количества операций загрузки
        this.form.loadingCounter = (this.form.loadingCounter != null ? this.form.loadingCounter : 0) + 1;
        this.setLoading(true);
    }

    /**
     * Установка состояния загрузки
     * @param loading идёт ли загрузка
     */
    setLoading(loading) {
        this.form.isLoading = loading;
    };
}