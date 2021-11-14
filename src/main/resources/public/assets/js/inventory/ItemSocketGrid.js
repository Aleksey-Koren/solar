function ItemSocketGrid(context) {
    var me = this;
    this.context = context;

    this.dropdown = Dom.el('select', {
        onchange: function (e) {
            var count = parseInt(me.amount.value);
            count = isNaN(count) ? 1 : count;
            while (count > 0) {
                count--;
                var v = parseInt(e.target.value);
                if(isNaN(v)) {
                    continue;
                }
                me.grid.data.push({
                    id: null,
                    itemId: null,
                    itemTypeId: v,
                    alias: null,
                    sortOrder: null
                });
            }
            me.grid.render();
            me.dropdown.value = '';
        }
    });
    this.amount = Dom.el('input', {value: 1});
    this.grid = new Grid({
        data: [],
        columns: [
            {
                name: 'typeTitle',
                title: 'Type',
                render: function(row, cache) {
                    if(!cache.typeMap) {
                        cache.typeMap = {};
                        me.context.stores.inventory.inventoryTypes.forEach(function (type) {
                            cache.typeMap[type.id] = type.title;
                        });
                    }
                    return cache.typeMap[row.itemTypeId] || "";
                }
            },
            {
                name: 'alias',
                title: 'Alias',
                render: function (row) {
                    return Dom.el('input', {
                        name: "alias",
                        value: row.alias || "",
                        onkeyup: function (e) {
                            row.alias = e.target.value;
                        }
                    });
                }
            },
            {
                name: 'sort',
                title: ' ',
                render: function (row) {
                    var up = Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            me.move(row, true);
                        }
                    }, 'UP');
                    var down = Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            me.move(row, false);
                        }
                    }, 'DOWN');
                    var content = [];
                    if (me.grid.data[0] === row) {
                        content.push(down);
                    } else if (me.grid.data[me.grid.data.length - 1] === row) {
                        content.push(up);
                    } else {
                        content.push(up);
                        content.push(" ");
                        content.push(down);
                    }
                    return Dom.el('div', {}, content);
                }
            },
            {
                name: 'delete',
                title: 'Delete',
                render: function (row) {
                    return Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            me.grid.data.splice(me.grid.data.indexOf(row), 1);
                            me.grid.render();
                        }
                    }, 'X')
                }
            }
        ]
    });
    this.grid.hidePagination();

    this.container = Dom.el('div', {}, [
        Dom.el('div', {}, "Sockets"),
        this.dropdown,
        ' ',
        this.amount,
        this.grid.container
    ]);
    var store = this.context.stores.inventory;
    store.listen(this);
    if (store.isTypesLoaded) {
        this.updateTypeComponents();
    } else {
        store.update('type');
    }
}

ItemSocketGrid.prototype.move = function (row, up) {
    var position = this.grid.data.indexOf(row);
    if (up) {
        if (position === 0) {
            return;
        }
        this.grid.data[position] = this.grid.data[position - 1];
        this.grid.data[position - 1] = row;
    } else {
        if (position === this.grid.data.length - 1) {
            return;
        }
        this.grid.data[position] = this.grid.data[position + 1];
        this.grid.data[position + 1] = row;
    }
    this.grid.render();
};

ItemSocketGrid.prototype.onStoreChange = function () {
    this.updateTypeComponents();
};

ItemSocketGrid.prototype.unmount = function () {
    this.context.stores.inventory.remove(this);
};

ItemSocketGrid.prototype.setData = function (data) {
    this.grid.data = data;
    this.updateTypeComponents();
};

ItemSocketGrid.prototype.updateTypeComponents = function () {
    Dom.clear(this.dropdown);
    var me = this;
    me.grid.render();
    me.dropdown.appendChild(Dom.el('option', {value: ''}, ''));
    this.context.stores.inventory.inventoryTypes.forEach(function (mod) {
        me.dropdown.appendChild(Dom.el('option', {value: mod.id}, mod.title))
    });
};