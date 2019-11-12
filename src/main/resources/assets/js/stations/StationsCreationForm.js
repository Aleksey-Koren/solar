function StationsCreationForm(id, onClose) {
    var w = wrapTableInput;

    var me = this;
    this.planets = Dom.el('select', {name: 'planetId'});


    this.id = id;
    this.onClose = onClose;
    this.title = Dom.el('h3', {}, !id ? "Create new Station" : "");
    this.form = Dom.el('form', {
        class: "stations-form", onsubmit: function (e) {
            me.onSubmit(e)
        }
    }, [
        Dom.el('input', {name: 'id', type: 'hidden'}),
        Dom.el('table', {}, [
            w('title'),
            w('fraction', Dom.el('select', {name: 'fraction'}, [
                Dom.el('option', {value: 'EU'}, 'Earth Union'),
                Dom.el('option', {value: 'PRC'}, 'People Republic of China'),
                Dom.el('option', {value: 'MA'}, 'Mars Military'),
                Dom.el('option', {value: 'AB'}, 'Asteroid Belt'),
                Dom.el('option', {value: 'PI'}, 'Pirates'),
                Dom.el('option', {value: 'JM'}, 'Jupiter Mechanics'),
                Dom.el('option', {value: 'V'}, 'Void')
            ])),
            w('type', Dom.el('select', {name: 'type'}, [
                Dom.el('option', {value: 'static'}, 'Static'),
                Dom.el('option', {value: 'mining'}, 'Mining'),
                Dom.el('option', {value: 'military'}, 'Military'),
                Dom.el('option', {value: 'science'}, 'Science'),
                Dom.el('option', {value: 'production'}, 'Production'),
                Dom.el('option', {value: 'asylum'}, 'Asylum')
            ])),
            w('population'),
            w('planet', Dom.el('div', {}, [
                this.planets,
                Dom.el('a', {
                    href: '#', onclick: function (e) {
                        e.preventDefault();
                        me.planets.value = '';
                    }
                }, 'X')])),
            w('x'),
            w('y'),
            w('angle'),
            w('aphelion'),
            w('orbitalPeriod'),
            w(' ', Dom.el('div', {}, [
                Dom.el('input', {
                    type: 'button', onclick: function () {
                        me.onClose()
                    }, value: "Back"
                }),
                ' ',
                Dom.el('input', {type: 'submit', value: "Save"})
            ]))
        ])
    ]);

    this.container = Dom.el('div', {});

    this.loadDropdown().then(function () {
        if (id) {
            me.load(id);
        }
    });
    this.productionGrid = new ProductionGrid();

    Dom.append(this.container, [
        this.title,
        Dom.el('div', 'flex-row', [
            this.form,
            this.productionGrid
        ])
    ]);


}

StationsCreationForm.prototype.loadDropdown = function () {
    var me = this;
    return Rest.doGet('/api/planet/utils/dropdown').then(function (options) {
        var value = me.planets.value;
        me.planets.innerHTML = '';
        Dom.append(me.planets, [Dom.el('option', {value: ""}, ""), options.map(function (opt) {
            return Dom.el('option', {value: opt.value}, opt.label);
        })]);
        me.planets.value = value;
    }).catch(function () {
        Notification.error("Can't load planets dropdown")
    });
};

StationsCreationForm.prototype.load = function () {
    var me = this;
    Rest.doGet('/api/station/' + this.id).then(function (value) {
        Dom.form(me.form, value);
        if (value.production) {
            me.productionGrid.grid.data = value.production;
            me.productionGrid.grid.render();
        }
    }).catch(function () {
        Notification.error("Can't load station with id " + me.id)
    });
};

StationsCreationForm.prototype.onSubmit = function (e) {
    if (e) e.preventDefault();
    var data = Dom.fromForm(this.form);
    data.production = this.productionGrid.getData();
    var me = this;
    Rest.doPost('/api/station', data).then(function (response) {
        me.onClose();
        Notification.info("Station was created (updated)");
    }).catch(function () {
        Notification.error("Can't create (update) station");
    });
};