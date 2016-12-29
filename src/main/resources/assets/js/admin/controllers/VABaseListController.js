export class VABaseListController {

    constructor(Restangular, baseUrl, loadObjectsFunction) {
        const that = this;

        // список
        that.objects = [];
        // базовый url
        that.baseUrl = baseUrl;
        // рестангуляр
        that.restAngular = Restangular;

        if (loadObjectsFunction != null) {
            loadObjectsFunction(that);
        } else {
            Restangular.all(baseUrl)
                .getList()
                .then(function successCallback(objects) {
                    that.objects = objects;
                }, function errorCallback(data) {

                });
        }
    }

    // добавить ответ в список
    addNewObj(o) {
        this.objects.push(angular.copy(o));
    }

    // заменить ответ по ID (например после обновление в форме редактирования
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

VABaseListController.$inject = ["Restangular"];
