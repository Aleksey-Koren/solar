function ObjectsGrid(context) {
    this.context = context;
    this.container = Dom.el('div');
    var me = this;
    this.form = null;
    this.grid = new Grid({
        data: [],
        columns: [
            {
                name: 'id',
                title: 'ID'
            },
            {
                name: 'type',
                title: 'Type',
                render: function(row, context) {
                    if(!context.types) {
                        context.types = {};
                        me.store.inventoryItems.forEach(function(item){
                            context.types[item.id] = item.title;
                        });
                    }
                    return context.types[row.hullId];
                }
            },
            {
                name: 'title',
                title: 'Title',
                render: function (row, context) {
                    return Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            me.showForm(row.id);
                        }
                    }, "Edit " + (row.title || context.types[row.hullId]))
                }
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
    this.store = this.context.stores.inventory;
    this.store.listen(this);
    if(!this.store.isItemsLoaded) {
        this.store.update(['item']);
    }
}

ObjectsGrid.prototype.onStoreChange = function() {
    this.grid.render();
};
ObjectsGrid.prototype.unmount = function() {
    if(this.form) {
        this.form.unmount();
    }
    this.store.remove(this);
};
ObjectsGrid.prototype.showGrid = function() {
    var me = this;
    Rest.doGet("/api/objects/config/?" + this.grid.queryString()).then(function(response){
        Dom.clear(me.container);
        me.container.appendChild(me.grid.container);
        me.grid.setPage(response);
        me.grid.render();
    })
};

ObjectsGrid.prototype.showForm = function(id){
    var me = this;
    var clb = function(){
        Rest.doGet("/api/objects/config/" + id).then(function(response) {

            var itemType = null;
            var types = me.store.inventoryTypes;
            for(var i = 0; i < types.length; i++) {
                if(types[i].title === 'hull') {
                    itemType = types[i].id;
                    break;
                }
            }
            if(itemType === null) {
                Notification.error("Can't find hull id");
                return;
            }
            Dom.clear(me.container);
            me.form = new ObjectsForm(response, me.context, function(){
                me.showGrid();
            }, {
                itemType: itemType,
                socketDescription: response.inventoryType,
                attachedToSocketDisabled: !response.inventoryType
            });
            me.container.appendChild(me.form.container);
        })
    };
    if(this.store.isTypesLoaded) {
        clb();
    } else {
        this.store.update('type').then(function(){
            clb();
        })
    }

};