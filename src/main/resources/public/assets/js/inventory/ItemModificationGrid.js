function ItemModificationGrid(context) {
    var me = this;
    this.context = context;

    this.dropdown = Dom.el('select', {onchange: function(e){
        me.addModification(e.target.value);
    }});
    this.grid = new Grid({
        data: [],
        columns: [
            {
                name: 'id',
                title: 'ID',
                render: function(row) {
                    return row.id ? row.id : "new"
                }
            },
            {
                name: 'title',
                title: 'title',
                render: function(row) {
                    return row.title + (row.data ? " (" + row.data + ")" : '');
                }
            },
            {
                name: 'delete',
                title: 'Delete',
                render: function(row){
                    return Dom.el('a', {href: '#', onclick: function(e){
                        e.preventDefault();
                        me.grid.data.splice(me.grid.data.indexOf(row), 1);
                        me.grid.render();
                        me.updateDropdown();
                    }}, 'X')
                }
            }
        ]
    });
    this.grid.hidePagination();

    this.container = Dom.el('div', {}, [
        Dom.el('div', {}, "Modifications"),
        this.dropdown,
        this.grid.container
    ]);
    var store = this.context.stores.inventory;
    store.listen(this);
    if(store.isModificationsLoaded) {
        this.updateDropdown();
    } else {
        store.update('modification');
    }
}

ItemModificationGrid.prototype.addModification = function(id) {
    id = parseInt(id);
    if(id && !isNaN(id)) {
        var list = this.context.stores.inventory.inventoryModifications;
        for(var i = 0; i < list.length; i++) {
            var item = list[i];
            if(item.id === id) {
                this.grid.data.push(item);
                this.grid.render();
                this.updateDropdown();
                break;
            }
        }
    }
};

ItemModificationGrid.prototype.onStoreChange = function() {
    this.updateDropdown();
};

ItemModificationGrid.prototype.setData = function(data) {
    this.grid.data = data;
    this.grid.render();
    this.updateDropdown();
};

ItemModificationGrid.prototype.updateDropdown = function() {
    this.dropdown.innerHTML = '';
    var me = this;
    var map = {};
    me.grid.data.forEach(function(row){
        map[row.id] = true;
    });
    me.dropdown.appendChild(Dom.el('option', {value: ''}, ''));
    this.context.stores.inventory.inventoryModifications.filter(function(mod){
        return !map[mod.id];
    }).forEach(function(mod) {
        me.dropdown.appendChild(Dom.el('option', {value: mod.id}, mod.title + (mod.data ? " (" + mod.data + ")" : '')))
    })
};

ItemModificationGrid.prototype.unmount = function() {
    this.context.stores.inventory.remove(this);
};
