function InventoryStore() {
    AbstractStore.call(this);
    this.inventoryTypes = [];
    this.inventoryItems = [];
    this.inventoryModifications = [];
    this.dropdown = [];
    this.name = "inventory";
    this.isTypesLoaded = false;
    this.isItemsLoaded = false;
    this.isModificationsLoaded = false;
}
InventoryStore.prototype = Object.create(AbstractStore.prototype);

InventoryStore.prototype.drop = function(row) {
    for(var i = 0; i < this.inventoryTypes.length; i++) {
        if(this.inventoryTypes[i].id === row.id) {
            this.inventoryTypes.splice(i, 1);
        }
        if(this.dropdown[i].id === row.id) {
            this.dropdown.splice(i, 1);
        }
    }
    this.notify({
        inventoryModifications: this.inventoryModifications,
        dropdown: this.dropdown,
        inventoryTypes: this.inventoryTypes
    });
};
InventoryStore.prototype.update = function(type) {
    var me = this;
    var promises = [];
    if(!type) {
        type = 'all';
    }
    if(type === 'type' || type === 'all') {
        promises.push(Rest.doGet('/api/inventory-type').then(function (value) {
            me.isTypesLoaded = true;
            me.inventoryTypes = value;
            me.dropdown = [];
            for (var i = 0; i < value.length; i++) {
                var p = value[i];
                me.dropdown.push({value: p.id, label: p.title});
            }
            return value;
        }));
    }
    if(type === 'item' || type === 'all') {
        promises.push(Rest.doGet('/api/inventory-item').then(function(value){
            me.isItemsLoaded = true;
            me.inventoryItems = value;
            return value;
        }));
    }
    if(type === 'modification' || type === 'all') {
        promises.push(Rest.doGet('/api/inventory-modification').then(function(value){
            me.isModificationsLoaded = true;
            me.inventoryModifications = value;
            return value;
        }));
    }

    Promise.all(promises).then(function(){
        me.notify({
            inventoryModifications: me.inventoryModifications,
            inventoryItems: me.inventoryItems,
            dropdown: me.dropdown,
            inventoryTypes: me.inventoryTypes
        });
    })
};
