/**
 * @param context {AppContext}
 * @param solarMap {SolarMap}
 * @constructor
 */
function SolarMapControls(context, solarMap) {
    this.context = context;
    this.solarMap = solarMap;
    this.container = Dom.el('div', 'solar-map-controls');
}

SolarMapControls.prototype.render = function () {
    Dom.append(document.body, this.container);
    this.container.append(new SolarMapCenterShip(this.solarMap).container);
    this.container.append(new SolarMapChatButton(this.solarMap).container);
};


SolarMapControls.prototype.unmount = function () {
    this.container.parentNode.removeChild(this.container);
};

