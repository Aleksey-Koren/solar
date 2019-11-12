function DashboardManagement() {
    this.container = Dom.el('div');
}


DashboardManagement.prototype.render = function() {
    this.container.innerHTML = "DASHBOARD"
};