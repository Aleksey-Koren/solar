/**
 * @param context {AppContext}
 * @param solarMap {SolarMap}
 * @constructor
 */
function SolarMapControls(context, solarMap) {
    this.context = context;
    this.solarMap = solarMap;
    this.container = Dom.el('div', 'solar-map-controls');
    this.chat = null;
}

SolarMapControls.prototype.render = function () {
    Dom.append(document.body, this.container);
    this.chat = new SolarMapChatButton(this.solarMap);
    this.container.append(new SolarMapCenterShip(this.solarMap).container);
    this.container.append(this.chat.container);
};


SolarMapControls.prototype.unmount = function () {
    this.container.parentNode.removeChild(this.container);
    this.chat.unmount();
};

