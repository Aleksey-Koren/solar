function PermissionTypesGrid(context) {
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
                title: 'Permission',
                render: function(row){
                    return Dom.el('input', {value: row.title, onchange: function(e){
                            row.title = e.target.value;
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
        }}, 'Add new Permission Type'), me.grid.container]);
    this.grid.hidePagination();
    var store = this.context.stores.permissions;
    store.listen(function(data){
        me.grid.data = data.permissionTypes;
        me.grid.render();
    });
    if(!store.isLoaded) {
        store.update();
    } else {
        me.grid.data = store.permissionTypes;
        me.grid.render();
    }
}


PermissionTypesGrid.prototype.update = function (row) {
    var me = this;
    Rest.doPost('/api/permissions', row).then(function(){
        me.context.stores.permissions.update();
    }).catch(function(){
        Notification.error("Can't save permission type, server error")
    })
};


PermissionTypesGrid.prototype.delete = function (row) {
    var idx = this.grid.data.indexOf(row);
    if(idx > -1) {
        this.grid.data.splice(idx, 1);
        this.grid.render();
    }
    if(!row.id) {
        return;
    }
    var me = this;
    Rest.doDelete('/api/inventory-type/' + row.id).then(function(){
        me.context.stores.permissions.update();
    }).catch(function(){
        Notification.error("Can't delete inventory type, server error")
    })
};

