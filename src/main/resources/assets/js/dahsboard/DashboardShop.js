function DashboardShop(context) {
    this.context = context;
    this.goods = new GoodsShop(context);
    this.ships = new StarShipShop(context);
    this.inventory = new InventoryShop(context);

    this.mounted = null;

    this.tab = Dom.el('div');
    var me = this;
    this.container = Dom.el('div', {}, [
        Dom.el('div', {}, [
            Dom.el('a', {
                href: '#', onclick: function (e) {
                    e.preventDefault();
                    me.mount(me.goods);
                }
            })
        ], 'Goods'),
        Dom.el('div', {}, [
            Dom.el('a', {
                href: '#', onclick: function (e) {
                    e.preventDefault();
                    me.mount(me.ships);
                }
            })
        ], 'Ships'),
        Dom.el('div', {}, [
            Dom.el('a', {
                href: '#', onclick: function (e) {
                    e.preventDefault();
                    me.mount(me.inventory);
                }
            })
        ], 'Inventory'),
        this.tab
    ]);
    this.mount(me.ships);
}


DashboardShop.prototype.unmount = function () {
    if(this.mounted) {
        this.mounted.unmount();
    }
};

DashboardShop.prototype.mount = function (element) {
    if(this.mounted !== element) {
        if(this.mounted) {
            this.mounted.unMount();
        }
        this.tab.innerHTML = '';
        this.tab.appendChild(element.container);
        element.mount();
    }
};