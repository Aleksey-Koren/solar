function GoodsShop(context) {
    AbstractShop.call(this, context);
}
GoodsShop.prototype = Object.create(AbstractShop.prototype);
GoodsShop.prototype.constructor = GoodsShop;

GoodsShop.prototype.onStoreChange = function() {
    this.items = this.context.stores.station.goods;
    this.renderItems();
};