function PlanetWindow(context) {
    this.container = Dom.el('div', {class: 'planet-window'});
    this.context = context;
    this.planet = null;
}

PlanetWindow.prototype.show = function(planet) {
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
        Dom.el('div', null, type)
    ];
    if(planet.type === 'planet') {
        content.push(Dom.el('div', null, planet.moons && planet.moons.length ? "Moons amount: " + planet.moons.length : "No moons"));
    }
    Dom.append(this.container, content);

};