/**
 * @param solarMap {SolarMap}
 * @constructor
 */
function SolarMapCenterShip( solarMap) {
    this.container = Dom.el('div', {class: 'solar-map-control', onclick: function(){
            var user = solarMap.context.stores.userStore.user;
            if (!user) {
                return;
            }
            var object = null;
            for(var i = 0; i < solarMap.objects.length; i++) {
                var obj = solarMap.objects[i];
                if (obj.obj.userId === user.user_id) {
                    object = obj;
                    break;
                }
            }
            if(object) {
                var zoom = 0.1 / solarMap.kkmPerPixel;
                var abs = solarMap.getAbsPoint(object.x, object.y);
                solarMap.centerMap(object);
                var newRel = solarMap.getRelPoint(abs.x, abs.y)
                solarMap.changeZoom(zoom, newRel.x, newRel.y);
            }
        }
    }, [
        new SolarMapIcon("solid", 'crosshairs').icon
    ]);
}
