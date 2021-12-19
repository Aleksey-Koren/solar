var SolarMapZoom = {
    changeZoom: function (me, zoom, relX, relY) {
        if(me.zoomStack.length > 0) {
            me.zoomStack.pop();
        }
        if (me.zoomStack.length > 4) {
            return;
        }
        var last = me.zoomStack.length > 1 ? me.zoomStack[me.zoomStack.length - 1] : null;
        if (last === null || (last && ((last.zoom > 1 && zoom > 1) || (last.zoom < 1 && last.zoom < 1)))) {
            me.zoomStack.push({
                absBefore: me.getAbsPoint(relX, relY),
                absZeroBefore: me.getAbsPoint(0, 0),
                zoom: zoom,
                kkm: me.kkmPerPixel,
                point: {x: relX, y: relY},
                time: new Date().getTime() + 2000,
                step: 0,
                maxStep: 20
            });
        }

    },
    calculateZoom: function(me) {
        if (me.zoomStack.length === 0) {
            return;
        }
        var zoomInfo = me.zoomStack[0];
        if (zoomInfo.step === zoomInfo.maxStep) {
            me.zoomStack.shift();
            if (me.zoomStack.length) {
                zoomInfo = me.zoomStack[0];
                zoomInfo.absZeroBefore = me.getAbsPoint(0, 0);
                zoomInfo.kkm = me.kkmPerPixel;
            } else {
                return;
            }
        }

        zoomInfo.step++;
        if (me.kkmPerPixel > 51918 && zoomInfo.zoom > 1) {
            me.zoomStack.shift();
            me.calculateZoom();
            return;
        }

        var zoom;
        if (zoomInfo.zoom > 1) {
            zoom = 1 + ((zoomInfo.zoom - 1) / zoomInfo.maxStep) * zoomInfo.step;
        } else {
            zoom = 1 - (((1 - zoomInfo.zoom) / zoomInfo.maxStep) * zoomInfo.step);
        }

        var absBefore = zoomInfo.absBefore;
        var absZeroBefore = zoomInfo.absZeroBefore;
        me.kkmPerPixel = zoomInfo.kkm * zoom;
        var absZeroAfter = me.getAbsPoint(0, 0);
        var dx = absZeroBefore.x - absZeroAfter.x;
        var dy = absZeroBefore.y - absZeroAfter.y;

        var zeroX = dx / me.kkmPerPixel;
        var zeroY = dy / me.kkmPerPixel;

        var windowShiftX, windowShiftY;
        dx = absBefore.x - absZeroBefore.x;
        dy = absBefore.y - absZeroBefore.y;
        if (zoomInfo.zoom < 1) {
            windowShiftX = (dx - dx * zoom) / me.kkmPerPixel;
            windowShiftY = (dy - dy * zoom) / me.kkmPerPixel;
        } else {
            windowShiftX = -(dx * (zoom - 1) / zoom) / zoomInfo.kkm;
            windowShiftY = -(dy * (zoom - 1) / zoom) / zoomInfo.kkm;
        }
        me.window.x += zeroX + windowShiftX;
        me.window.y += zeroY + windowShiftY;
    }
}