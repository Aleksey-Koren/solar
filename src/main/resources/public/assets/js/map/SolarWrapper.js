function SolarWrapper(context) {
    /**
     * @var context AppContext
     */
    this.context = context;
    this.planets = [];
    var me = this;
    this.map = new SolarMap(context);
    this.container = Dom.el('div', {class: 'solar-wrapper'}, this.map.canvas);
    requestAnimationFrame(function(){me.draw()})
}

SolarWrapper.prototype.unmount = function() {
    this.map.unmount();
    this.context.stores.planets.remove(this)
};

SolarWrapper.prototype.render = function() {
    var store = this.context.stores.planets;
    store.listen(this);
    if(!store.isLoaded) {
        store.update();
    } else {
        this.planets = store.planets;
    }
};

SolarWrapper.prototype.draw = function() {
    this.map.render(this.planets);
    var me = this;
    requestAnimationFrame(function(){me.draw()});
};

SolarWrapper.prototype.onStoreChange = function() {
    this.context.stores.planets.remove(this)
};

SolarWrapper.prototype.onStoreChange = function(obj, storeName) {
    if (storeName === this.context.stores.planets.name) {
        this.planets = obj.list;
    }
};
