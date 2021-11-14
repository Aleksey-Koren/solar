/**
 *
 * @param context
 * @param params {{name: '', value: '', disabled: boolean, staticInventoryType: int}}
 * @constructor
 */
function ObjectSelector(context, params) {
    var me = this;
    this.filter = {
        inventoryType: params.staticInventoryType || 0
    };
    this.selector = new GenericSelector({
        disabled: params.disabled,
        context: context,
        popupTitle: "Select Object",
        value: params.value,
        name: params.name,
        title: "Select Object",
        onRowClick: function(row) {
            me.selector.update(row.title, row.id);
        },
        onValueUpdate: function(value) {
            me.onValueUpdate(value);
        },
        onPopupOpen: function(){
            me.showGrid();
        },
        onPaginationChange: function(){
            me.showGrid();
        },
        columns: [
            {
                name: "id",
                title: "ID",
            },
            {
                name: "title",
                title: "Title",
                render: function(row, context) {
                    if(row.title) {
                        return row.title;
                    }
                    if(!context.titles) {
                        context.titles = {};
                        me.store.inventoryItems.forEach(function(item){
                            context.titles[item.id] = context.title;
                        })
                    }
                    return context.titles[row.hullId] || "-"
                }
            },
            {
                name: "active",
                title: "Active",
                render: function(row) {
                    return row.active ? "+" : "-"
                }
            },
            {
                name: "userId",
                title: "User",
                render: function(row) {
                    return row.userId ? "Used by " + row.userId : "No user"
                }
            },
            {
                name: "status",
                title: "Status"
            }
        ],
        filters: [
            params.staticInventoryType ? null : Dom.el('select', {name: 'inventoryType', onchange: function(e){
                me.filter.inventoryType = e.target.value;
                me.selector.grid.pageInfo.page = 1;
                me.showGrid();
            }}, [Dom.el('option', {value: 0}, 'Object Type'),
                context.stores.inventory.inventoryTypes.map(function(item){
                    return Dom.el('option', {value: item.id}, item.title);
                })])
        ]
    });
    this.container = this.selector.container;
    this.store = context.stores.inventory;
    this.store.listen(this);
    if(!this.store.isItemsLoaded) {
        this.store.update('item');
    }
}

ObjectSelector.prototype.unmount = function() {
    this.selector.popup.hide();
    this.store.remove(this);
};

ObjectSelector.prototype.onStoreChange = function() {
    this.selector.grid.render();
};

ObjectSelector.prototype.showGrid = function() {
    var grid = this.selector.grid;
    grid.data = [];
    grid.render();
    var params = [];
    if(this.filter.inventoryType) {
        params.push("inventoryType=" + encodeURIComponent(this.filter.inventoryType));
    }
    Rest.doGet("/api/objects/config?" + params.join("&")).then(function(page){
        grid.setPage(page);
        grid.render();
    });
};

ObjectSelector.prototype.onValueUpdate = function(value) {
    if(!value) {
        return;
    }
    var me = this;
    Rest.doGet("/api/objects/config/" + value).then(function(object){
        me.selector.update(object.title || object.id, object.id);
    });
};
