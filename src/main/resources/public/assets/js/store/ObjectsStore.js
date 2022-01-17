function ObjectsStore() {
    AbstractStore.call(this);
    this.objects = [];
    this.ship = null;
    this.name = "objects"
}
ObjectsStore.prototype = Object.create(AbstractStore.prototype);


ObjectsStore.prototype.update = function() {
    var me = this;
    Rest.doGet('/api/star_map/user/view').then(function (value) {
        me.isLoaded = true;
        me.objects = value;
        me.notify({
            objects: me.objects
        });
    });
};
