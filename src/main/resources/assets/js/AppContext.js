function AppContext() {
    this.body = Dom.el('div');
    this.stores = {
        planets: new PlanetsStore(),
        inventory: new InventoryStore(),
        permissions: new PermissionsStore(),
        station: new StationStore(),
    };
    this.escListener = new EscListener();
    this.app = {
        instance: null,
        name: null
    };
    this.loginStorage = localStorage.getItem('rememberLogin') === '1' ? localStorage : sessionStorage;
    this.menu = null;
    window.appContext = this;
}