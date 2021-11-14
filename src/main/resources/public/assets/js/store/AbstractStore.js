function AbstractStore() {
    this.bus = [];
    this.isLoaded = false;
    this.name = "abstract-store"
}



AbstractStore.prototype.notify = function(obj) {
    var me = this;
    this.bus.forEach(function(clb) {
        if(typeof clb === "function") {
            clb(obj);
        } else if(clb.onStoreChange) {
            clb.onStoreChange(obj, me.name);
        } else {
            console.error(clb);
            throw new Error("instance have no appropriate method for store callback");
        }
    })
};

AbstractStore.prototype.remove = function(clb) {
    var l = this.bus.length;
    while(l--) {
        if(this.bus[l] === clb) {
            this.bus.splice(l, 1);
        }
    }
};

AbstractStore.prototype.listen = function(clb) {
    if(this.bus.length > 10) {
        console.error('too many subscriptions for store', this);
    }
    if(typeof clb === 'function' || typeof clb.onStoreChange === 'function') {
        if (this.bus.indexOf(clb) === -1) {
            this.bus.push(clb);
        }
    } else {
        throw new Error("clb not a function (planets store)");
    }
};