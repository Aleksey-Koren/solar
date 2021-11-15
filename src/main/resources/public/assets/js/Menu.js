function Menu(context) {
    this.context = context;
    this.container = Dom.el('div', 'head-menu head-menu-top');
    var logout = this.createItem("logoutManagement", "Logout", function(){return new LogoutManagement(context)});
    logout.style.cssFloat = 'right';
    Dom.append(this.container, [
        this.createItem('solarWrapper', 'Star Map', function(){return new SolarWrapper(context)}),
        this.createItem('dashboard', 'Dashboard', function(){return new DashboardManagement(context)}),
        this.createDropdown("Config", [
            this.createItem('planetsManagement', 'Planets Management', function(){return new PlanetsManagement(context)}),
            this.createItem('productsManagement', 'Products Management', function(){return new ProductsManagement()}),
            this.createItem('stationsManagement', 'Stations Management', function(){return new StationsManagement(context)}),
            this.createItem('inventoryManagement', 'Inventory Management', function(){return new InventoryManagement(context)}),
            this.createItem('permissionsManagement', 'Permissions Management', function(){return new PermissionsManagement(context)}),
            this.createItem('usersManagement', 'Users Management', function(){return new UserManagement(context)})
        ]),
        logout
    ])
}

Menu.prototype.createDropdown = function (title, content) {
    var timeout = null;
    var el = Dom.el('span', {class: "hidden", onclick: function(){
        Dom.addClass(el, 'hidden');
    }}, content);
    return Dom.el('a', {
        href: '#',
        class: "head-menu-item",
        onmouseover: function () {
            clearTimeout(timeout);
            Dom.removeClass(el, 'hidden');
        },
        onmouseleave: function(){
            timeout = setTimeout(function(){
                Dom.addClass(el, 'hidden')
            }, 1000);
        }
    }, [title, el])
};

Menu.prototype.createItem = function (appName, appTitle, factory) {
    var me = this;
    return Dom.el(
        'a',
        {
            href: '#', onclick: function (e) {
                e.preventDefault();
                me.runApp(appName, factory);
            }
        },
        appTitle
    );
};

Menu.prototype.runApp = function(appName, factory) {
    var app = this.context.app;
    if (app.name !== appName) {
        app.name = appName;

        if(app.instance && app.instance.unmount) {
            app.instance.unmount();
        }
        app.instance = factory();
        this.context.body.innerHTML = '';
        this.context.body.appendChild(app.instance.container);
        app.instance.render();
    }
};