function InventoryItemForm(context, user, openGrid) {
    this.context = context;
    this.openGrid = function(){
        openGrid();
        me.unmount();
    };
    var w = wrapTableInput;
    this.inventoryType = Dom.el('select', {name: 'inventoryType'});
    var me = this;
    this.form = Dom.el('form', {
        class: 'inventory-item-form',
        onsubmit: function (e) {
            e.preventDefault();
            me.save();
        }
    }, [
        Dom.el('input', {type: 'hidden', name: 'id'}),
        w('title'),
        w('Inventory Type', this.inventoryType),
        w('powerMin'),
        w('powerMax'),
        w('powerDegradation'),
        w('cooldown'),
        w('distance'),
        w('energyConsumption'),
        w('durability'),
        w('mass'),
        w('price'),
        w('description', Dom.el('textarea', {name: 'description', rows: 4})),
        w(' ', Dom.el('div', {}, [
            Dom.el('input', {type: 'button', value: 'Back', onclick: function(){
                me.context.stores.inventory.remove(me);
                openGrid();
            }}),
            ' ',
            Dom.el('input', {type: 'submit', value: 'Save'})
        ]))
    ]);

    this.modificationsGrid = new ItemModificationGrid(context);
    this.socketsGrid = new ItemSocketGrid(context);
    this.container = Dom.el('div', {class: "items-f-left"}, [
        this.form,
        this.modificationsGrid.container,
        this.socketsGrid.container
    ]);
    this.user = user;
    this.init = false;
    var inventoryStore = context.stores.inventory;
    inventoryStore.listen(this);
    if (inventoryStore.isTypesLoaded) {
        this.updateSelect();
    } else {
        inventoryStore.update('type');
    }
    this.loadData(user);
}


InventoryItemForm.prototype.onStoreChange = function () {
    this.updateSelect();
};

InventoryItemForm.prototype.updateSelect = function () {
    var value = this.inventoryType.value;
    if(!value && !this.init) {
        this.init = true;
        value = this.user.inventoryType;
    }
    this.inventoryType.innerHTML = '';
    Dom.append(this.inventoryType, Dom.el('option', {value: ''}, ''));
    var me = this;
    this.context.stores.inventory.inventoryTypes.forEach(function (type) {
        Dom.append(me.inventoryType, Dom.el('option', {value: type.id}, type.title));
    });
    this.inventoryType.value = value;
};

InventoryItemForm.prototype.save = function() {
    var item = Dom.fromForm(this.container);
    item.modifications = this.modificationsGrid.grid.data;
    item.sockets = this.socketsGrid.grid.data;
    var me = this;
    Rest.doPost('/api/inventory-item', item).then(function(){
        me.context.stores.inventory.update('item');
        me.context.stores.inventory.remove(me);
        me.openGrid();
    }).catch(function(){
        Notification.error("Can't save inventory item, server error");
    })
};

InventoryItemForm.prototype.unmount = function() {
    this.modificationsGrid.unmount();
    this.socketsGrid.unmount();
};

InventoryItemForm.prototype.loadData = function(item) {
    if(item && item.id) {
        var me = this;
        Rest.doGet('/api/inventory-item/' + item.id).then(function (item) {
            Dom.form(me.form, item);
            me.modificationsGrid.setData(item.modifications);
            me.socketsGrid.setData(item.sockets);
        }).catch(function () {
            Notification.error("Can't load inventory item, server error");
        });
    }
};