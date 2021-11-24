function PlanetWindow(context) {
    this.container = Dom.el('div', {class: 'planet-window'});
    this.context = context;
    this.planet = null;
    this.stack = [];
}

PlanetWindow.prototype.show = function(planet, keepLink) {
    if(keepLink) {
        if(this.planet) {
            this.stack.push(this.planet);
        }
    } else {
        this.stack = [];
    }
    var me = this;
    this.planet = planet;
    Dom.append(this.context.body, this.container);
    Dom.clear(this.container);
    var type;
    switch (planet.type) {
        case 'planet':
            type = 'Planet: ' + planet.title;
            break;
        case 'moon':
            type = 'Moon: ' + planet.title;
            break;
        case 'star':
            type = 'Sun';
            break;
        default:
            type = '';
    }
    var content = [
        Dom.el('div', null, type),
        new CollapsibleWindow('Orbital information', this.orbitalInformation(), true).container,
        new CollapsibleWindow('Surface information', this.surfaceInformation()).container,
        planet.moons && planet.moons.length ? new CollapsibleWindow("Satellites", this.moonsInformation()) : null,
        this.stack.length ? Dom.el('a', {
            'href': '#',
            class: 'planet-window-back',
            click: function(e){e.preventDefault(); me.planet = null; me.show(me.stack.pop(), true)}},
            ['Back to ', this.stack[this.stack.length - 1].title]) : null
    ];

    Dom.append(this.container, content);

};

PlanetWindow.prototype.moonsInformation = function() {
    var me = this;
    var moons = [].concat(this.planet.moons);
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
    var p = this.planet;
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
    var p = this.planet;
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