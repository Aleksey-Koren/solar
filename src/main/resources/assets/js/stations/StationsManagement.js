function StationsManagement(context) {
    this.grid = new StationsGrid(context);
    this.container = Dom.el('div', {}, [this.grid.container])
}

StationsManagement.prototype.render = function() {
    this.grid.mount();
};
StationsManagement.prototype.unmount = function() {
    this.grid.unmount();
};