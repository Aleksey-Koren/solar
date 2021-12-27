function PlanetWindow(context) {
    this.container = Dom.el('div', {class: 'planet-window'});
    this.context = context;
    this.object = null;
    this.stack = [];
}

PlanetWindow.prototype.show = function(object, keepLink) {
    if(keepLink) {
        if(this.planet) {
            this.stack.push(this.planet);
        }
    } else {
        this.stack = [];
    }
    var me = this;
    this.object = object;
    Dom.append(this.context.body, this.container);
    Dom.clear(this.container);
    var type;
    var planetoid;
    var real = object.obj;
    switch (object.type) {
        case 'planet':
            type = 'Planet: ' + real.title;
            planetoid = true;
            break;
        case 'moon':
            type = 'Moon: ' + real.title;
            planetoid = true;
            break;
        case 'star':
            type = 'Sun';
            planetoid = true;
            break;
        case 'ship':
            type = 'Starship: ' + real.title;
            planetoid = false;
            break;
        case 'station':
            type = 'Station: ' + real.title;
            planetoid = false;
            break;
        default:
            type = '';
    }
    var content = [
        Dom.el('div', null, type),
        planetoid && new CollapsibleWindow('Orbital information', this.orbitalInformation(), true).container,
        planetoid && new CollapsibleWindow('Surface information', this.surfaceInformation()).container,
        planetoid && real.moons && real.moons.length ? new CollapsibleWindow("Satellites", this.moonsInformation()) : null,
        planetoid && this.stack.length ? Dom.el('a', {
            'href': '#',
            class: 'planet-window-back',
            click: function(e){e.preventDefault(); me.planet = null; me.show(me.stack.pop(), true)}},
            ['Back to ', this.stack[this.stack.length - 1].title]) : null,
        object.type === 'ship' && new CollapsibleWindow('Spacecraft information', JSON.stringify(real), false),
        object.type === 'station' && new CollapsibleWindow('Fraction: ', JSON.stringify(real), true),
        object.type === 'station' && new CollapsibleWindow('Marketplace information', JSON.stringify(real), false),
        object.type === 'station' && new CollapsibleWindow('Technical information', JSON.stringify(real), false),
    ];

    Dom.append(this.container, content);

};

PlanetWindow.prototype.moonsInformation = function() {
    var me = this;
    var moons = [].concat(this.object.obj.moons);
    moons.sort(function(m1, m2) {
        return m1.title.localeCompare(m2.title);
    })
    return Dom.el('div', {class: 'planet-window-satellites'}, moons.map(m => {
        return Dom.el('div', null, Dom.el('a', {
                'href': '#',
                click: function(e){e.preventDefault(); me.show(m, true)}},
            [m.title]))
    }))
}
PlanetWindow.prototype.surfaceInformation = function() {
    var p = this.object.obj;
    return Dom.el('div', null, [
        Dom.el('div', null, ["Mass: ", p.mass]),
        Dom.el('div', null, ["Volume: ", p.volume]),
        Dom.el('div', null, ["Mean Radius: ", p.meanRadius]),
        Dom.el('div', null, ["Surface Gravity: ", p.surfaceGravity]),
        Dom.el('div', null, ["Surface Pressure: ", p.surfacePressure]),
        Dom.el('div', null, ["Escape Velocity: ", p.escapeVelocity]),
    ])
}
PlanetWindow.prototype.orbitalInformation = function() {
    var p = this.object.obj;
    return Dom.el('div', null, [
        Dom.el('div', null, ["Aldebo: ", p.aldebo]),
        Dom.el('div', null, ["Aphelion: ", p.aphelion]),
        Dom.el('div', null, ["Perihelion: ", p.perihelion]),
        Dom.el('div', null, ["Axial Tilt: ", p.axialTilt]),
        Dom.el('div', null, ["Eccentricity: ", p.eccentricity]),
        Dom.el('div', null, ["Inclination: ", p.inclination]),
        Dom.el('div', null, ["Mean Anomaly: ", p.meanAnomaly]),
        Dom.el('div', null, ["Mean Orbit Radius: ", p.meanOrbitRadius]),
        Dom.el('div', null, ["Orbital Period: ", p.orbitalPeriod, " (Earth Days)"]),
    ])
}