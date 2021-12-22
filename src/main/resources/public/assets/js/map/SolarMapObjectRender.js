var SolarMapObjectRender = {
    shapes: {
        ship: [[10, 5], [-10, 0], [10, -5]],
        station: [[15, 10], [-5, 15], [-20, 0], [-5, -15], [15, -10]],
        unknown: [[10, 10], [-10, 10], [-10, -10], [10, -10]],
    },
    /**
     *
     * @param me {SolarMap}
     * @param ctx {CanvasRenderingContext2D}
     * @param object {BaseObject}
     * @param absWindow {Line[]}
     * @param zero
     */
    drawObject: function(me, ctx, object, absWindow, zero) {
        var user = me.context.stores.userStore.user;
        if (!user) {
            return;
        }
        var type = me.types[object.hullId];
        if(!type) {
            return;
        }
        type = type === 'SHIP' ? 'ship' : (type === 'STATION' ? 'station' : 'unknown')

        ctx.strokeStyle = object.userId === user.user_id ? '#ff1b1b' : '#4fb169';
        ctx.beginPath();
        var radius = 30;

        var bound1 = absWindow[0].a
        var bound2 = absWindow[2].a;
        if(object.x < bound1.x || object.x > bound2.x || object.y < bound1.y || object.y > bound2.y) {
            console.log('out of bounds')
            return;
        }
        var rel = me.getRelPoint(object.x, object.y);

        me.objects.push({
            type: type,
            obj: object,
            x: rel.x,
            y: rel.y,
            r: radius
        });
        var tr = SolarMapObjectRender.shapes[type];

        ctx.moveTo(rel.x + tr[tr.length - 1][0], rel.y +  tr[tr.length - 1][1]);
        for(var i = 0; i < tr.length; i++) {
            ctx.lineTo(rel.x + tr[i][0], rel.y + tr[i][1]);
        }
        if(object.userId !== user.user_id && me.kkmPerPixel <= 50) {
            ctx.strokeText(object.title, rel.x, rel.y, {color: ctx.strokeStyle});
        }
        ctx.stroke();

    }
}