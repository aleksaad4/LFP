import urls from "../urls";

export default class AccountService {
    constructor(state, restAngular) {
        const that = this;

        that.state = state;
        that.restAngular = restAngular;
    };

    /**
     * Сохранение аккаунта в сервис
     * @param account аккаунт
     */
    setAccount(account) {
        this.account = account;
    }

    /**
     * Выход к странице логина
     */
    logout() {
        const that = this;

        // шлём запрос на logout
        that.restAngular.one(urls.logout).get()
            .then(function success() {
                location.reload();
            }, function error(data) {

            });
    }
}

AccountService.$inject = ["$state", "Restangular"];