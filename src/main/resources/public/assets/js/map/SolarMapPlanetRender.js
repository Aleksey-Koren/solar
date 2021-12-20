var SolarMapPlanetRender = {
    /**
     *
     * @param me {SolarMap}
     * @param ctx {CanvasRenderingContext2D}
     * @param planet {Planet}
     * @param absWindow {Line[]}
     * @param zero
     */
    drawPlanetOrbit: function(me, ctx, planet, absWindow, zero) {

        var drawNormal = me.kkmPerPixel > 50 || (me.kkmPerPixel <= 50 && planet.type === 'moon');
        var smallSize = me.kkmPerPixel < 4.5;
        var points = null;
        if (!drawNormal) {
            points = OrbitGeometry.getIntersections(planet, absWindow, zero);
            if (!smallSize) {
                drawNormal = points.length;
            }
        }
        ctx.strokeStyle = '#555555';
        if (drawNormal) {
            ctx.beginPath();
            var dist = planet.aphelion / me.kkmPerPixel;
            var relP = me.getRelPoint(zero.x, zero.y);
            ctx.arc(relP.x, relP.y, dist, 0, Math.PI * 2);
            ctx.stroke();
        } else if (smallSize && points && points.length > 1) {
            ctx.beginPath();
            var p0 = me.getRelPoint(points[0].x, points[0].y);
            var p1 = me.getRelPoint(points[1].x, points[1].y);
            ctx.moveTo(p0.x, p0.y);
            ctx.lineTo(p1.x, p1.y);
            ctx.stroke();
        }
    },
    /**
     *
     * @param me {SolarMap}
     * @param ctx {CanvasRenderingContext2D}
     * @param planet {Planet}
     * @param absWindow {{x:number, y:number}[]}
     * @param zero: {{x: number, y: number}}
     */
    drawPlanet: function(me, ctx, planet, absWindow, zero) {
        if (planet.type === 'moon') {
            if (!planet.aphelion || me.kkmPerPixel > 250) {
                return;
            }
        }

        SolarMapPlanetRender.drawPlanetOrbit(me, ctx, planet, absWindow, zero);
        if (planet.type === 'star') {
            ctx.strokeStyle = '#ffc562';
        } else if (planet.type === 'planet') {
            ctx.strokeStyle = '#4fb169';
        } else {
            ctx.strokeStyle = 'rgba(151,151,151,0.58)';
        }
        var angle = SolarTimer.angle(planet);
        var absX = Math.cos(angle) * planet.aphelion + zero.x;
        var absY = Math.sin(angle) * planet.aphelion + zero.y;


        ctx.beginPath();
        var radius = planet.meanRadius / 1000 / me.kkmPerPixel;
        if (radius < 10) {
            radius = 10;
        }
        var rel = me.getRelPoint(absX, absY);
        me.objects.push({
            type: 'planet',
            obj: planet,
            x: rel.x,
            y: rel.y,
            r: radius
        });
        ctx.arc(rel.x, rel.y, radius, 0, Math.PI * 2);
        ctx.strokeText(planet.title, rel.x, rel.y, {color: ctx.strokeStyle});
        ctx.stroke();
        if (planet.moons && planet.moons.length) {
            var newZero = {x: absX, y: absY};
            planet.moons.forEach(function (moon) {
                me.drawPlanet(ctx, moon, absWindow, newZero)
            })
        }
    }
}