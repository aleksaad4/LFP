export default class MenuController {

    constructor(scope, state, rootScope) {
        const that = this;

        that.scope = scope;
        that.state = state;
        that.rootScope = rootScope;

        // всё меню
        that.menu = scope.rCtrl.menu;

        // текущее состояние меню
        that.menuState = {
            selectedIndex: -1,
            selectedSubMenuIndex: -1,
            activeMenu: {}
        };

        // инициализация меню
        that.initMenu();
    }

    /**
     * Функция инициализации меню
     */
    initMenu() {
        const that = this;

        if (that.menu != null) {
            // подписываемся на событие изменения состояния
            that.rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                that.onStateChange();
            });
        }

        // обрабатываем текущее состояние
        that.onStateChange();
    }

    /**
     * Функция обработки текущего состояния
     */
    onStateChange() {
        const that = this;

        if (that.menu != null) {
            let justFirstLevel = false;

            // определим какой из верхних пунктов меню выбран
            for (let i = 0; i < that.menu.length; i++) {
                let obj = that.menu[i];
                // если state включет в себя url из меню
                if (that.state.includes(obj.url)) {
                    if (that.state.current.name == obj.url) {
                        // если это точное совпадение
                        // выбрана непосредственно текущий пункт меню, а не какой-то из подпунктов
                        justFirstLevel = true;
                    }
                    // отмечаем этот верхний пункт меню как выбранный
                    that.selectMenuItem(i);
                    break;
                }
            }

            // текущий выбранный пункт меню
            let activeMenu = that.menuState.activeMenu;
            if (activeMenu == null) {
                // если никакой не выбран, то выбираем первый
                that.selectMenuItem(0);
                justFirstLevel = true;
                activeMenu = that.menu.activeMenu;
            }

            // теперь пройдемся по меню второго уровня (если оно есть)
            let inSubmenu = false;
            if (activeMenu != null && activeMenu.subMenu != null) {
                // находим выбранный пункт подменю
                for (let i = 0; i < activeMenu.subMenu.length; i++) {
                    let obj = activeMenu.subMenu[i];
                    if (that.state.includes(activeMenu.url + "." + obj.url)) {
                        inSubmenu = true;
                        that.menuState.selectedSubMenuIndex = i;
                        break;
                    }
                }
            }

            // если мы не находимся в подменю, а путь указывает только на верхний пункт
            // и подменю вообще есть - тогда пойдем в первый его пункт
            if (!inSubmenu && justFirstLevel && activeMenu.subMenu != null && activeMenu.subMenu.length > 0) {
                const defaultState = that.menuState.activeMenu.url + "." + that.menuState.activeMenu.subMenu[0].url;
                this.state.go(defaultState);
            }
        }
    }

    /**
     * Выбор элемента из меню первого уровня
     * @param i индекс
     */
    selectMenuItem(i) {
        this.menuState.selectedIndex = i;
        this.menuState.activeMenu = this.menu[i];
    }

    /**
     * Обработка клика на элемент меню первого уровня
     * @param i
     */
    menuItemClick(i) {
        this.selectMenuItem(i);
        if (this.menuState.activeMenu.subMenu == null || this.menuState.activeMenu.subMenu.length == 0) {
            this.state.go(this.menuState.activeMenu.url);
        }
    }

    /**
     * Имеет ли пункт меню первого уровня подпункты
     * @param i
     * @returns {boolean}
     */
    menuItemHaveSubItem(i) {
        return this.menu[i].subMenu != null && this.menu[i].subMenu.length > 0;
    }

    /**
     * Выбран ли текущий пункт меню первого уровня?
     * @param i
     * @returns {boolean}
     */
    isMenuItemSelected(i) {
        return this.menuState.selectedIndex === i;
    };

    /**
     * Выбран ли текущий пункт меню второго уровня?
     * @param parentIndex
     * @param index
     * @returns {boolean}
     */
    isSubMenuItemSelected(parentIndex, index) {
        return (parentIndex == null || this.isMenuItemSelected(parentIndex)) && this.menuState.selectedSubMenuIndex === index;
    };
}

MenuController.$inject = ["$scope", "$state", "$rootScope", "$stateParams", "Restangular"];
