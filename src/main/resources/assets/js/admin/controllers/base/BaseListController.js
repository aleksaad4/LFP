export class BaseListController {

    constructor(restAngular, baseUrl) {
        const that = this;

        that.restAngular = restAngular;

        // список объектов
        that.objects = [];
        // url для работы с объектами
        that.baseUrl = baseUrl;

        // начинаем загрузку объектов
        that.restAngular.all(baseUrl)
            .getList()
            .then(function successCallback(objects) {
                that.objects = objects;
            }, function errorCallback(data) {

            });
    }

    // добавить объект в список
    addNewObj(o) {
        this.objects.push(angular.copy(o));
    }

    // заменить ответ по ID (например, после обновление в форме редактирования)
    replaceObj(o) {
        for (let i = 0; i < this.objects.length; i++) {
            const obj = this.objects[i];
            if (obj.id == o.id) {
                // копируем объект, чтобы отвязать объект в списке, от объекта в форме редактирования
                angular.copy(o, this.objects[i]);
                break;
            }
        }
    }

    // удалить ответ из списка
    deleteObj(o) {
        for (let i = 0; i < this.objects.length; i++) {
            const obj = this.objects[i];
            if (obj.id == o.id) {
                // удалить объект из массива
                this.objects.splice(i, 1);
                break;
            }
        }
    }
}

BaseListController.$inject = ["Restangular"];