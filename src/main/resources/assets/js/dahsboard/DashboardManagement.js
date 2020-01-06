function DashboardManagement(context) {
    this.container = Dom.el('div');
    this.shop = null;
    this.context = context
}


DashboardManagement.prototype.render = function() {
    this.container.innerHTML = "";
    this.shop = new DashboardShop(this.context);
    this.container.appendChild(this.shop.container);
};
DashboardManagement.prototype.unmount = function() {
    if(this.shop) {
        this.shop.unmount();
    }
};