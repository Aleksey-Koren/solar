function StarShipShop(context) {
    AbstractShop.call(this, [context]);
}
StarShipShop.prototype = Object.create(AbstractShop.prototype);
StarShipShop.prototype.constructor = StarShipShop;

StarShipShop.prototype.onStoreChange = function() {
    this.items = this.context.stores.station.ships;
    this.renderItems();
};