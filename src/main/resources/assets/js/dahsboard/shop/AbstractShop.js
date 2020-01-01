function AbstractShop(context) {
    this.table = Dom.el('table');
    this.controls = Dom.el('div');
    this.items = [];
    this.container = Dom.el("div", {}, [
        this.table,
        this.controls
    ]);
    this.context = context;
    context.stores.station.listen(this)
}

AbstractShop.prototype.renderItems = function() {
    this.table.innerHTML = '';
    var colIdx = 0;
    var row = null;
    for(var i = 0; i < this.items.length; i++) {
        if(colIdx % 5 === 0) {
            row = Dom.el('tr');
            this.table.appendChild(row);
        }
        Dom.append(row, this.createCell(this.items[i]))
    }
};

AbstractShop.prototype.loadData = function() {
    this.context.stores.station.update();
};

AbstractShop.prototype.dataLoaded = function() {
    return this.context.stores.station.isLoaded;
};

AbstractShop.prototype.onStoreChange = function() {
    throw new Error("Not implemented, return type - void");
};

AbstractShop.prototype.createCell = function(row) {
    var out = Dom.el('div', {class: 'shop-item', click: function() {
            row.isSelected = !row.isSelected;
            out.className = row.isSelected ? 'shop-item shop-item-selected' : 'shop-item';
        }}, row[i].title);
    return out;
};

AbstractShop.prototype.mount = function() {
    if(this.dataLoaded()) {
        this.renderItems()
    } else {
        this.loadData().catch(function() {
            Notification.error("Could not load data for shop");
        })
    }
};
AbstractShop.prototype.unmount = function() {
    this.context.stores.station.remove(this);
};