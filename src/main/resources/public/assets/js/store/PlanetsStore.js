function PlanetsStore() {
    AbstractStore.call(this);
    this.planets = [];
    this.map = {};
    this.dropdown = [];
    this.name = "planets"
}
PlanetsStore.prototype = Object.create(AbstractStore.prototype);

PlanetsStore.prototype.find = function(id) {
    return this.map[id] || null
};
PlanetsStore.prototype.update = function() {
    var me = this;
    Rest.doGet('/api/planet').then(function (value) {
        me.isLoaded = true;
        me.planets = value;
        me.dropdown = [];
        var map = {};
        for(var i = 0; i < value.length; i++) {
            var p = value[i];
            map[p.id] = p;
            me.dropdown.push({value: p.id, label: p.title});
        }
        for(var j = 0; j < value.length; j++) {
            var child = value[j];
            if(map[child.parent]) {
                var parent = map[child.parent];
                if(!parent.moons) {
                    parent.moons = [];
                }
                parent.moons.push(child);
            }
        }
        me.map = map;
        me.notify({
            list: me.planets,
            map: map,
            dropdown: me.dropdown
        })
    });
};