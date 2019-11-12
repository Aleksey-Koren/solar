function InventoryItemGrid(context) {
    this.context = context;
    this.container = Dom.el('div');
    var me = this;
    this.filterSelect = null;
    this.filterBy = '';
    this.grid = new Grid({
        data: [],
        columns: [
            {
                name: 'id',
                title: 'ID'
            },
            {
                name: 'title',
                title: 'Title',
                render: function (row) {
                    return Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            me.showForm(row);
                        }
                    }, "Edit " + row.title)
                }
            },
            {
                name: 'power',
                title: 'Power',
                render: function (row) {
                    return [row.powerMin, row.powerMax].filter(function (v) {
                            return v
                        }).join(' - ') +
                        (row.powerDegradation ? " (degradation: " + (parseInt(row.powerDegradation * 100) / 100) + ")" : "")
                }
            },
            {
                name: 'cooldown',
                title: 'Cooldown'
            },
            {
                name: 'energyConsumption',
                title: 'Energy Consumption'
            },
            {
                name: 'distance',
                title: 'Distance'
            },
            {
                name: 'durability',
                title: 'Durability'
            },
            {
                name: 'mass',
                title: 'Mass'
            },
            {
                name: 'price',
                title: 'Price'
            },
            {
                name: 'delete',
                title: 'Delete',
                render: function (row) {
                    return Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            if (!row.id || confirm("Are you sure to delete " + row.title + "?")) {
                                me.delete(row)
                            }
                        }
                    }, 'X');
                }
            }
        ]
    });

    this.showGrid();
    this.grid.hidePagination();
    var store = this.context.stores.inventory;
    store.listen(function () {
        me.drawStore();
    });
    if (!store.isItemsLoaded || !store.isTypesLoaded) {
        store.update();
    } else {
        this.drawStore();
    }
}


InventoryItemGrid.prototype.drawStore = function () {
    var store = this.context.stores.inventory;
    var me = this;
    this.grid.data = this.filterBy ? store.inventoryItems.filter(function(v){return v.inventoryType === me.filterBy}) : store.inventoryItems;
    this.grid.render();
    var value = this.filterSelect.value;
    this.filterSelect.innerHTML = '';
    Dom.append(this.filterSelect, Dom.el('option', {value: ''}, 'Filter by Inventory Type'));
    Dom.append(this.filterSelect, store.dropdown.map(function(opt){
        return Dom.el('option', {value: opt.value}, opt.label);
    }));
    this.filterSelect.value = value;
};

InventoryItemGrid.prototype.showGrid = function () {
    var me = this;
    me.container.innerHTML = '';
    if(!this.filterSelect) {
        this.filterSelect = Dom.el('select', {
            onchange: function () {
                me.filterBy = parseInt(me.filterSelect.value);
                if (isNaN(me.filterBy)) {
                    me.filterBy = '';
                }
                me.drawStore();
            }
        });
    }
    Dom.append(me.container, [Dom.el('a', {
        href: '#', click: function (e) {
            e.preventDefault();
            me.showForm({});
        }
    }, 'Add new Item'), ' ', this.filterSelect, me.grid.container]);
};
InventoryItemGrid.prototype.showForm = function (user) {
    var me = this;
    me.container.innerHTML = '';
    Dom.append(me.container, new InventoryItemForm(this.context, user, function () {
        me.showGrid();
    }));
};


InventoryItemGrid.prototype.update = function (row) {
    var me = this;
    Rest.doPost('/api/inventory-item', row).then(function () {
        me.context.stores.inventory.update('item');
    }).catch(function () {
        Notification.error("Can't save inventory type, server error")
    })
};


InventoryItemGrid.prototype.delete = function (row) {
    var idx = this.grid.data.indexOf(row);
    if (idx > -1) {
        this.grid.data.splice(idx, 1);
        this.grid.render();
    }
    if (!row.id) {
        return;
    }
    var me = this;
    Rest.doDelete('/api/inventory-item/' + row.id).then(function (response) {
        me.context.stores.inventory.update('item');
    }).catch(function () {
        Notification.error("Can't delete inventory type, server error")
    })
};

