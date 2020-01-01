function DashboardManagement() {
    this.container = Dom.el('div');
    this.shop = null;
}


DashboardManagement.prototype.render = function() {
    this.container.innerHTML = "";
    //this.shop = new DashboardShop();
   // this.container.appendChild(this.shop.container);
};
DashboardManagement.prototype.unmount = function() {
    if(this.shop) {
        this.shop.unmount();
    }
};