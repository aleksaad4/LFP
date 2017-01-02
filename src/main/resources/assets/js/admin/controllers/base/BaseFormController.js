import {BaseController} from "./BaseController";

export class BaseFormController extends BaseController {

    constructor() {
        super();
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
            fileUploader.clearQueue();
        };

        // обработчик успешной загрузки
        fileUploader.onSuccessItem = function (item, response, status, headers) {
            // тут нужно проверить статус ответа от сервера
            if (response.status == 'OK') {
                // успешно завершаем загрузку
                that.handleSuccess(false);
                fileUploader.clearQueue();
                // вызываем success-callback
                if (success != null) {
                    success(response.result);
                }
            } else {
                // завершаем загрузку не успешно
                that.handleError(response, fieldName);
                fileUploader.clearQueue();
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
}