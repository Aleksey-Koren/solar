function SolarWrapper(context) {
    /**
     * @var context AppContext
     */
    this.context = context;
    this.planets = [];
    this.radar = [];
    this.types = {};
    var me = this;
    this.map = new SolarMap(context, this.types);
    this.container = Dom.el('div', {class: 'solar-wrapper'}, this.map.canvas);
    requestAnimationFrame(function(){me.draw()})
    this.radarInterval = null;
}

SolarWrapper.prototype.unmount = function() {
    this.map.unmount();
    this.context.stores.planets.remove(this)
    this.context.stores.objects.remove(this)
    clearInterval(this.radarInterval)
};

SolarWrapper.prototype.render = function() {
    var planetsStore = this.context.stores.planets;
    planetsStore.listen(this);
    if(!planetsStore.isLoaded) {
        planetsStore.update();
    } else {
        this.planets = planetsStore.planets;
    }
    var objectsStore = this.context.stores.objects;
    objectsStore.listen(this);
    if(!objectsStore.isLoaded) {
        objectsStore.update();
    } else {
        this.radar = objectsStore.radar;
    }

    this.radarInterval = setInterval(function(){
        objectsStore.update();
    }, 5000);
    objectsStore.update();


    var inventory = this.context.stores.inventory;
    inventory.listen(this);

    if(!inventory.isTypesLoaded) {
        inventory.update(['item']);
    } else {
        this.defineTypes();
    }
};

SolarWrapper.prototype.draw = function() {
    this.map.render(this.planets, this.radar);
    var me = this;
    requestAnimationFrame(function(){me.draw()});
};

SolarWrapper.prototype.onStoreChange = function() {
    this.context.stores.planets.remove(this)
};

SolarWrapper.prototype.defineTypes = function() {
    var keys = Object.keys(this.types);
    var me = this;
    keys.forEach(function(key) {
        delete me.types[key];
    });
    this.context.stores.inventory.inventoryItems.forEach(function(inventoryItem) {
        me.types[inventoryItem.id] = inventoryItem.type;
    });
}
SolarWrapper.prototype.onStoreChange = function(obj, storeName) {
    if (storeName === this.context.stores.planets.name) {
        this.planets = obj.list;
    } else if (storeName === this.context.stores.objects.name) {
        this.radar = obj.objects;
    } else if(storeName === this.context.stores.inventory.name) {
        this.defineTypes()
    }
};
