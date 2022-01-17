/**
 * @param solarMap {SolarMap}
 * @constructor
 */
function SolarMapChatButton( solarMap) {
    var me = this;
    me.chat = new ChatMain(solarMap);
    this.container = Dom.el('div', {class: 'solar-map-control', onclick: function(){
            me.chat.showChat();
        }
    }, [
        new SolarMapIcon("solid", 'comments').icon
    ]);
}

SolarMapChatButton.prototype.unmount = function() {
    this.chat.unmount();
}