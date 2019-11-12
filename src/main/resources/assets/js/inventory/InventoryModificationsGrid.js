function InventoryModificationsGrid(context) {
    this.context = context;
    this.container = Dom.el('div');
    var me = this;
    this.grid = new Grid({
        data: [],
        columns: [
            {
                name: 'id',
                title: 'ID'
            },
            {
                name: 'title',
                title: 'Type',
                render: function(row){
                    return Dom.el('input', {value: row.title, onchange: function(e){
                            row.title = e.target.value;
                            me.update(row);
                        }})
                }
            },
            {
                name: 'data',
                title: 'Data',
                render: function(row){
                    return Dom.el('input', {value: row.data, onchange: function(e){
                            row.data = e.target.value;
                            me.update(row);
                        }})
                }
            },
            {
                name: 'delete',
                title: 'Delete',
                render: function(row) {
                    return Dom.el('a', {href: '#', onclick: function(e){
                        e.preventDefault();
                        if(!row.id || confirm("Are you sure to delete " + row.title + "?")) {
                            me.delete(row)
                        }
                    }}, 'X');
                }

            }
        ]
    });
    Dom.append(me.container, [Dom.el('a', {href: '#', click: function(e){
        e.preventDefault();
        me.grid.data.push({title: ''});
        me.grid.render();
    }}, 'Add new Modification'), me.grid.container]);
    this.grid.hidePagination();
    var store = this.context.stores.inventory;
    store.listen(function(data){
        me.grid.data = data.inventoryModifications;
        me.grid.render();
    });
    if(!store.isModificationsLoaded) {
        store.update('modification');
    } else {
        me.grid.data = store.inventoryModifications;
        me.grid.render();
    }
}


InventoryModificationsGrid.prototype.update = function (row) {
    var me = this;
    Rest.doPost('/api/inventory-modification', row).then(function(){
        me.context.stores.inventory.update('modification');
    }).catch(function(){
        Notification.error("Can't save inventory modification, server error")
    })
};


InventoryModificationsGrid.prototype.delete = function (row) {
    var idx = this.grid.data.indexOf(row);
    if(idx > -1) {
        this.grid.data.splice(idx, 1);
        this.grid.render();
    }
    if(!row.id) {
        return;
    }
    var me = this;
    Rest.doDelete('/api/inventory-modification/' + row.id).then(function(response){
        me.context.stores.inventory.update('modification');
    }).catch(function(){
        Notification.error("Can't delete inventory type, server error")
    })
};

