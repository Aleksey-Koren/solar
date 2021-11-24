function InventoryStore() {
    AbstractStore.call(this);
    this.inventoryTypes = [];
    this.inventoryItems = [];
    this.inventoryModifications = [];
    this.dropdown = [];
    this.name = "inventory";
    /*shield, hull, laser*/
    this.isTypesLoaded = false;
    /*shild mk1, shield mk2, neutron blaster, aluminium armor*/
    this.isItemsLoaded = false;
    /*+50% damage to shields*/
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
InventoryStore.prototype.update = function(type, params) {
    if(typeof type === 'string') {
        type = [type];
    }
    var me = this;
    var promises = [];
    if(!type) {
        type = ['all'];
    }
    if(type.indexOf('type') > -1 || type.indexOf('all') > -1) {
        promises.push(Rest.doGet('/api/inventory-type?page=0&size=1000').then(function (response) {
            const value = response.content;
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
    if(type.indexOf('item') > -1 || type.indexOf('all') > -1) {
        promises.push(Rest.doGet('/api/inventory-item').then(function(value){
            me.isItemsLoaded = true;
            me.inventoryItems = value;
            return value;
        }));
    }
    if(type.indexOf('modification') > -1 || type.indexOf('all') > -1) {
        promises.push(Rest.doGet('/api/inventory-modification').then(function(value){
            me.isModificationsLoaded = true;
            me.inventoryModifications = value;
            return value;
        }));
    }

    return Promise.all(promises).then(function(){
        me.notify({
            inventoryModifications: me.inventoryModifications,
            inventoryItems: me.inventoryItems,
            dropdown: me.dropdown,
            inventoryTypes: me.inventoryTypes
        });
        return true;
    })
};
