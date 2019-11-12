function StationsManagement() {
    this.grid = new StationsGrid();
    this.container = Dom.el('div', {}, [this.grid.container])
}

StationsManagement.prototype.render = function() {
    this.grid.mount();
};