function ProductsManagement() {
    this.grid = new ProductsGrid();
    this.container = Dom.el('div', {}, [this.grid.container])
}

ProductsManagement.prototype.render = function() {
    this.grid.mount();
};