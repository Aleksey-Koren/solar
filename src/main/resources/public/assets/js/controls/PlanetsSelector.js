/**
 *
 * @param context
 * @param params {{name: '', value: ''}}
 * @constructor
 */
function PlanetsSelector(context, params) {
    var me = this;
    this.filter = {
        planet: '',
        parent: ''
    };
    this.selector = new GenericSelector({
        context: context,
        popupTitle: "Select Planet",
        value: params.value,
        name: params.name,
        onRowClick: function(row) {
            me.selector.update(row.title, row.id);
        },
        onValueUpdate: function(value) {
            me.onValueUpdate(value);
        },
        onPopupOpen: function(){
            me.showGrid();
        },
        onPaginationChange: function(){
            me.showGrid();
        },
        columns: [
            {
                name: "id",
                title: "ID",
            },
            {
                name: "title",
                title: "Title",
            },
            {
                name: "type",
                title: "Type",
            },
            {
                name: "parent",
                title: "Parent",
                render: function(row){
                    var parent = context.stores.planets.find(row.parent);
                    if(parent) {
                        return parent.title;
                    }
                }
            },

        ],
        filters: [
            Dom.el('input', {name: 'pName', placeholder: 'Planet Title', onkeyup: function(e){
                me.filter.planet = e.target.value.toLowerCase();
                me.selector.grid.pageInfo.page = 1;
                me.onStoreChange();
            }}),
            " ",
            Dom.el('input', {name: 'parent', placeholder: 'Planet Parent', onkeyup: function(e){
                me.filter.parent = e.target.value.toLowerCase();
                me.selector.grid.pageInfo.page = 1;
                me.onStoreChange();
            }}),
        ]
    });
    this.container = this.selector.container;
    this.store = context.stores.planets;
    this.store.listen(this);
}



PlanetsSelector.prototype.unmount = function() {
    this.store.remove(this);
    this.selector.popup.hide();
};

PlanetsSelector.prototype.showGrid = function() {
    if(!this.store.isLoaded) {
        this.store.update();
    } else {
        this.onStoreChange();
    }
};

PlanetsSelector.prototype.onValueUpdate = function() {
    this.showGrid();
};

PlanetsSelector.prototype.onStoreChange = function() {
    var pageInfo = this.selector.grid.pageInfo;
    var data = [];
    var storeData = this.store.planets;
    var me = this;
    if(this.filter.parent) {
        var parents = {};
        var allowedParents = storeData.filter(function(planet){
            return planet.title.toLowerCase().indexOf(me.filter.parent) === 0;
        });
        if(allowedParents.length) {
            allowedParents.forEach(function(planet){
                parents[planet.id] = true;
            });
            storeData = storeData.filter(function(planet){
                return parents[planet.parent];
            });
        } else {
            storeData = [];
        }
    }
    if(this.filter.planet) {
        storeData = storeData.filter(function(planet) {
            return planet.title.toLowerCase().indexOf(me.filter.planet) === 0;
        })
    }
    var start = (pageInfo.page - 1) * pageInfo.size;
    var stop = Math.min(start + pageInfo.size, storeData.length);
    for(var i = start; i < stop; i++) {
        data.push(storeData[i]);
    }
    this.selector.grid.setPage({
        content: data,
        totalElements: storeData.length
    });
    this.selector.grid.render();

    var value = this.selector.input.value;
    if(value) {
        var planet = this.store.find(value);
        if(planet) {
            this.selector.update(planet.title, planet.id);
        }
    }
};