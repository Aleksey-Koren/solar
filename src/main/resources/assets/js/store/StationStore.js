function StationStore() {
    AbstractStore.call(this);
    this.init();
    this.ships = [];
    this.goods = [];
    this.inventory = [];
}
StationStore.prototype = Object.create(AbstractStore.prototype);

StationStore.prototype.init = function() {
    this.ships = [];
    this.ships = [];
    this.ships = [];
    this.isLoaded = false;
};

StationStore.prototype.clean = function() {
    this.init();
    this.notify();
};

StationStore.prototype.update = function() {
    var me = this;
    Rest.doGet('/api/station/user/marketplace').then(function (value) {
        me.isLoaded = true;
        me.ships = value.ships;
        me.inventory = value.inventory;
        me.goods = value.goods;
        me.notify()
    });
};