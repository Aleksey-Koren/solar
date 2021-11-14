/**
 *
 * @param context
 * @param params {{name: '', value: '', itemType: int, onObjectSelect: function}}
 * @constructor
 */
function ObjectDescriptionSelector(context, params) {
    var me = this;
    this.filter = {
        title: ''
    };
    this.itemType = params.itemType;
    if(!this.itemType) {
        throw new Error("No item type in object description selector");
    }
    this.selector = new GenericSelector({
        context: context,
        popupTitle: "Select Hull",
        value: params.value,
        name: params.name,
        onRowClick: function(row) {
            me.selector.update(row.title, row.id);
            if(params.onObjectSelect)params.onObjectSelect(row);
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
            },
            {
                name: "description",
                title: "Description",
            },
            {
                name: "durability",
                title: "Durability",
            },
            {
                name: "price",
                title: "Price",
            },
            {
                name: "mass",
                title: "Mass",
            }
        ],
        filters: [
            Dom.el('input', {name: 'hullName', placeholder: 'Hull Title', onkeyup: function(e){
                me.filter.title = e.target.value.toLowerCase();
                me.selector.grid.pageInfo.page = 1;
                me.onStoreChange();
            }}),
        ]
    });
    this.container = this.selector.container;
    this.store = context.stores.inventory;
    this.store.listen(this);
}



ObjectDescriptionSelector.prototype.unmount = function() {
    this.store.remove(this);
    this.selector.popup.hide();
};

ObjectDescriptionSelector.prototype.showGrid = function() {
    if(!this.store.isItemsLoaded) {
        this.store.update(['item', 'type']);
    } else {
        this.onStoreChange();
    }
};

ObjectDescriptionSelector.prototype.onValueUpdate = function() {
    this.showGrid();
};

ObjectDescriptionSelector.prototype.onStoreChange = function() {
    var pageInfo = this.selector.grid.pageInfo;
    var data = [];
    var itemType = this.itemType;
    var storeData = this.store.inventoryItems.filter(function(item) {
        return item.inventoryType === itemType;
    });

    var me = this;
    if(this.filter.title) {
        storeData = storeData.filter(function(item) {
            return item.title.toLowerCase().indexOf(me.filter.title) === 0;
        })
    }
    var start = (pageInfo.page - 1) * pageInfo.size;
    var stop = Math.min(start + pageInfo.size, storeData.length);
    for(var i = start; i < stop; i++) {
        data.push(storeData[i]);
    }
    this.selector.grid.setPage({
        content: data,
        totalElements: storeData.length
    });
    this.selector.grid.render();

    var value = this.selector.input.value;
    if(value) {
        var item = null;
        for(var k = 0; k < this.store.inventoryItems.length; k++) {
            if(this.store.inventoryItems[k].id === parseInt(value)) {
                item = this.store.inventoryItems[k];
                break;
            }
        }
        if(item) {
            this.selector.update(item.title, item.id);
        }
    }
};