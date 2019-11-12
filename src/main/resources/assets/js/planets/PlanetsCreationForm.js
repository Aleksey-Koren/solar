function PlanetsCreationForm(context) {
    var w = wrapTableInput;

    this.context = context;
    this.rawPlanets = null;
    this.list = this.createPlanetsGrid();

    this.type = Dom.el('select', {name: 'type'}, [
        Dom.el('option', {value: 'star'}, 'Star'),
        Dom.el('option', {value: 'planet'}, 'Planet'),
        Dom.el('option', {value: 'moon'}, 'Moon')
    ]);
    this.parents = Dom.el('select', {name: 'parent'});
    this.gUrl = Dom.el('a', {href:'#', target: '_blank'}, "Search Google");
    this.form = Dom.el('form', {
        class: "planets-form", onsubmit: function (e) {
            me.onSubmit(e)
        }
    }, [
        Dom.el('input', {name: 'id', type: 'hidden'}),
        Dom.el('table', {}, [
            w(' ', this.gUrl),
            w('title'),
            w('type', this.type),
            w('aldebo'),
            w('aphelion'),
            w('angle'),
            w('axialTilt'),
            w('eccentricity'),
            w('escapeVelocity'),
            w('inclination'),
            w('mass'),
            w('meanAnomaly'),
            w('meanOrbitRadius'),
            w('meanRadius'),
            w('orbitalPeriod'),
            w('perihelion'),
            w('siderealRotationPeriod'),
            w('surfaceGravity'),
            w('surfacePressure'),
            w('volume'),
            w('parent', this.parents),
            w(' ', Dom.el('input', {type: 'submit', value: "Save"}))
        ])
    ]);

    this.container = Dom.el('div', {});
    var me = this;
    this.moonsGrid = this.createPlanetsGrid();
    Dom.addClass(this.moonsGrid.container, "hidden");

    this.gridC = Dom.el('div', {}, this.list.container);
    this.formC = Dom.el('div', {class: 'hidden flex-row'}, [
        this.form,
        this.moonsGrid.container/*,
        Dom.el('div', {}, [
            this.dataContainer,
            Dom.el('a', {
                href: '#', onclick: function (e) {
                    e.preventDefault();
                    Rest.doGet('/assets/planets.json').then(function (r) {
                        me.dataContainer.innerHTML = '';
                        me.rawPlanets = r;
                        Dom.append(me.dataContainer, me.fillData(r, ['star', 'planet', 'moon']));
                    })
                }
            }, 'Load raw data')
        ])*/
    ]);

    this.toggle = Dom.el('a', {
        href: '#', onclick: function (e) {
            e.preventDefault();
            if(me.toggle.formShown) {
                me.closeForm();
            } else {
                me.showForm();
            }
        }
    }, 'New Planet');
    Dom.append(this.container, [
        this.toggle,
        this.gridC,
        this.formC
    ]);
}



PlanetsCreationForm.prototype.showList = function(data) {
    this.list.data = data.filter(function (value) {
        return value.type === 'star' || value.type === 'planet'
    });
    this.list.render();
    var model = Dom.fromForm(this.form);
    if(model.id && model.type !== 'moon') {
        var id = parseInt(model.id);
        this.moonsGrid.data = data.filter(function(planet){
            return planet.parent === id;
        }).sort(function(p1, p2){
            if(!p1.title) {
                return -1;
            }
            return p1.title.localeCompare(p2.title);
        });
        this.moonsGrid.render();
    }
};
PlanetsCreationForm.prototype.mount = function() {
    this.loadDropdown();
    var store = this.context.stores.planets;
    var me = this;
    this.showListRef = function(store){
        me.showList(store.list);
    };
    store.listen(this.showListRef);

    if(!store.isLoaded) {
        store.update();
    } else {
        me.showList(store.planets);
    }
};
PlanetsCreationForm.prototype.unmount = function() {
    var store = this.context.stores.planets;
    store.remove(this.showListRef);
};

PlanetsCreationForm.prototype.createPlanetsGrid = function() {
    var me = this;
    var out = new Grid({columns: [
        {name: 'title', title: 'Title', render: function (row) {
                return Dom.el('span', {}, [Dom.el('a', {
                    href: '', onclick: function (e) {
                        e.preventDefault();
                        me.displayData(row, true);
                    }
                }, 'Edit ' + row.title), !row.aphelion || !row.angle || !row.orbitalPeriod ? Dom.el('span', {style: 'color: red'}, '*'): null])
            }},
        {name: 'angle', title: 'Angle'},
        {name: 'meanRadius', title: 'Mean Radius'},
        {name: 'aphelion', title: 'Aphelion (1000km)'},
        {name: 'orbitalPeriod', title: 'Orbital Period (days)'},
        {
            name: 'moons', title: 'Moons', render: function (row) {
                return row.moons ? row.moons.length : " - "
            }
        }
    ]});
    out.hidePagination();
    return out;
};

PlanetsCreationForm.prototype.showForm = function () {
    this.toggle.formShown = true;
    Dom.clearForm(this.form);
    Dom.removeClass(this.formC, 'hidden');
    Dom.addClass(this.gridC, 'hidden');
    this.toggle.innerText = 'Show grid'
};
PlanetsCreationForm.prototype.showGrid = function () {
    this.toggle.formShown = false;
    Dom.clearForm(this.form);
    Dom.removeClass(this.gridC, 'hidden');
    Dom.addClass(this.formC, 'hidden');
    this.toggle.innerText = 'New Planet'
};

PlanetsCreationForm.prototype.loadDropdown = function () {
    var me = this;
    Rest.doGet('/api/planet/utils/dropdown').then(function (r) {
        me.options = [];
        me.assignIdToRawData(r, me.rawPlanets);
        var p = me.parents.value;
        me.parents.innerHTML = '';
        Dom.append(me.parents, Dom.el('option', {value: ''}, 'none'));
        r.forEach(function (option) {
            Dom.append(me.parents, Dom.el('option', {value: option.value}, option.label));
        });
        me.parents.value = p;
    })
};
PlanetsCreationForm.prototype.assignIdToRawData = function (dropdown, planet) {
    if (planet) {
        var me = this;
        for (var i = 0; i < dropdown.length; i++) {
            var name = dropdown[i].label;
            if (planet.title === name) {
                planet.id = dropdown[i].value;
                if (planet.moons) {
                    planet.moons.forEach(function (moon) {
                        me.assignIdToRawData(dropdown, moon);
                    })
                }
                break;
            }
        }
    }
};

PlanetsCreationForm.prototype.fillData = function (planets, types) {
    types = [].concat(types);
    planets.type = types.shift();
    var me = this;
    var moons = planets.moons && planets.moons.length ? Dom.el('ol', "hidden", (planets.moons || []).map(function (p) {
        return Dom.el('li', {}, me.fillData(p, types));
    })) : null;
    return Dom.el('div', {}, [
        Dom.el('a', {
            href: '#', onclick: function (e) {
                e.preventDefault();
                me.displayData(planets, false);
            }
        }, planets.title),
        " ",
        moons ? Dom.el('a', {
            href: '#', onclick: function (e) {
                e.preventDefault();
                if (Dom.hasClass(moons, 'hidden')) {
                    Dom.removeClass(moons, 'hidden')
                } else {
                    Dom.addClass(moons, 'hidden')
                }
            }
        }, "Show moons") : null,
        moons
    ].filter(function (v) {
        return v
    }));
};

PlanetsCreationForm.prototype.closeForm = function () {
    var me = this;
    var parentId = parseInt(me.parents.value);
    var type = me.type.value;
    if(type === 'moon' && parentId > 0)  {
        var planets = me.context.stores.planets.planets;
        for(var i = 0; i < planets.length; i++) {
            if(planets[i].id === parentId) {
                me.displayData(planets[i], false);
                return;
            }
        }
    }
    me.showGrid()
};

PlanetsCreationForm.prototype.displayData = function (planet, loadDropdown) {
    this.showForm();
    var me = this;
    Dom.form(this.form, planet);
    this.gUrl.href = 'https://www.google.com/search?q=' + planet.title + (planet.type === 'moon' ? (function(){
        if(planet.parent) {
            var ops = me.parents.options;
            var l = ops.length;
            while(l--) {
                var op = ops[l];
                if(parseInt(op.value) === planet.parent) {
                    return ' ' + op.innerText;
                }
            }
        }
        return '';
    })() : '');
    if(loadDropdown)this.loadDropdown();
    if(planet.moons) {
        this.moonsGrid.data = planet.moons.sort(function(m1, m2){
            if(!m1.title) return -1;
            return m1.title.localeCompare(m2.title)
        });
        this.moonsGrid.render();
        Dom.removeClass(this.moonsGrid.container, "hidden");
    } else {
        Dom.addClass(this.moonsGrid.container, "hidden");
    }
};
PlanetsCreationForm.prototype.onSubmit = function (e) {
    if (e) e.preventDefault();
    var data = Dom.fromForm(this.form);
    var me = this;
    Rest.doPost('/api/planet', data).then(function (response) {
        me.context.stores.planets.update();
        me.closeForm();
    }).catch(function(){
        Notification.error("Can't save planet, server error")
    });
};