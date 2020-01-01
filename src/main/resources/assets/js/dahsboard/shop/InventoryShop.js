function InventoryShop(context) {
    AbstractShop.call(this, [context]);
}
InventoryShop.prototype = Object.create(AbstractShop.prototype);
InventoryShop.prototype.constructor = InventoryShop;

InventoryShop.prototype.onStoreChange = function() {
    this.items = this.context.stores.station.inventory;
    this.renderItems();
};