/**
 * @param solarMap {AppContext}
 * @constructor
 */
function SolarMapChatButton( context) {
    var me = this;
    me.chat = new ChatMain(context);
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