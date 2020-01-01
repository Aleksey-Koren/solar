function SolarMap(context) {
    this.context = context;
    this.zoom = 1;
    this.objects = [];
    this.canvas = Dom.el('canvas', {});
    this.ctx = this.canvas.getContext('2d');
    var realFillText = this.ctx.fillText;
    var me = this;
    this.ctx.fillText = function (text, x, y, params) {
        var stroke = null;
        var fill = null;
        if (params.color) {
            stroke = me.ctx.strokeStyle;
            fill = me.ctx.fillStyle;
            me.ctx.strokeStyle = params.color;
            me.ctx.fillStyle = params.color;
        }
        realFillText.call(me.ctx, text, x, y, params.maxWidth);
        if (stroke || fill) {
            me.ctx.strokeStyle = stroke;
            me.ctx.fillStyle = fill;
        }
    };
    this.ctx.strokeText = this.ctx.fillText;

    this.kkmPerPixel = 0;

    this.window = {
        dx: 0,
        dy: 0,
        x: 0,
        y: 0,
        width: 0,
        height: 0
    };

    this.drag = new DragHelper();
    this.drag.listen(this.canvas, function (x, y) {
        me.window.dx = x;
        me.window.dy = y;
    }, function (x, y) {
        me.window.dx = 0;
        me.window.dy = 0;
        me.window.x -= x;
        me.window.y -= y;
    });

    me.mx = 0;
    me.my = 0;

    this.canvas.addEventListener('mousemove', function (e) {
        me.mx = e.clientX;
        me.my = e.clientY;
        var hover = [];
        for(var i = 0; i < me.objects.length; i++) {
            var obj = me.objects[i];
            if(obj.x - obj.r <= e.clientX && obj.x + obj.r >= e.clientX && obj.y - obj.r <= e.clientY && obj.y + obj.r >= e.clientY) {
                hover.push(obj);
            }
        }
        if(hover.length === 1) {
            me.selectedObject = hover[0];
        } else if(hover.length > 1) {
            var obj1 = null;
            var p = -1;
            for(var h = 0; h < hover.length; h++) {
                var hh = hover[h];
                if(p < SolarMap.PRIORITES[hh.type]) {
                    p = SolarMap.PRIORITES[hh.type];
                    obj1 = hh;
                }
            }
            me.selectedObject = obj1;
        } else {
            me.selectedObject = null;
        }
    });

    this.zoomStack = [];

    this.resizeRef = function () {
        me.resize();
    };
    window.addEventListener('resize', this.resizeRef);
    this.init();

    this.canvas.addEventListener('wheel', function (e) {
        var change = 2;
        var x = me.selectedObject ? me.selectedObject.x : e.clientX;
        var y = me.selectedObject ? me.selectedObject.y : e.clientY;

        if (e.deltaY > 0) {
            me.changeZoom(change, x, y);
        } else if (e.deltaY < 0) {
            me.changeZoom(1 / change, x, y);
        }
    });
}

SolarMap.MAX_RADIUS_KKM = 15000000;
SolarMap.PRIORITES = {
    'moon': 1,
    'planet': 2,
    'sun': 3,
    'station': 4,
    'ship': 5
};

SolarMap.prototype.getAbsPoint = function (x, y) {
    return {
        x: (this.window.x - this.window.dx + x) * this.kkmPerPixel,
        y: (this.window.y - this.window.dy + y) * this.kkmPerPixel
    };
};

SolarMap.prototype.getRelPoint = function (x, y) {
    return {
        x: (x / this.kkmPerPixel) - this.window.x + this.window.dx,
        y: (y / this.kkmPerPixel) - this.window.y + this.window.dy
    }
};


SolarMap.prototype.render = function (planets) {
    this.calculateZoom();
    var me = this;
    var ctx = this.ctx;
    ctx.font = "14px Arial";
    ctx.clearRect(0, 0, this.window.width, this.window.height);
    ctx.beginPath();
    ctx.strokeStyle = '#4fb169';
    ctx.strokeText("Mouse: " + this.mx + "/" + this.my, 10, 70, {color: '#4fb169'});
    var ap = this.getAbsPoint(this.mx, this.my);
    var az = this.getAbsPoint(0, 0);
    ctx.strokeText("Zero: " + drawInt(az.x) + "/" + drawInt(az.y), 10, 110, {color: '#4fb169'});
    ctx.strokeText("Point: " + drawInt(ap.x) + "/" + drawInt(ap.y), 10, 90, {color: '#4fb169'});
    ctx.strokeText("KM per pixel: " + (this.kkmPerPixel * 1000), 10, 130, {color: '#4fb169'});
    ctx.stroke();
    ctx.save();

    this.objects = [];
    SolarTimer.iteration();
    ctx.textAlign = "center";

    var w = this.window;
    var absWindowPoints = [this.getAbsPoint(0, 0), this.getAbsPoint(w.width, 0), this.getAbsPoint(w.width, w.height), this.getAbsPoint(0, w.height)];
    var absWindow = [
        {a: absWindowPoints[0], b: absWindowPoints[1]},
        {a: absWindowPoints[1], b: absWindowPoints[2]},
        {a: absWindowPoints[2], b: absWindowPoints[3]},
        {a: absWindowPoints[3], b: absWindowPoints[0]}
    ];
    var zero = {x: 0, y: 0};
    if (planets.length) {
        me.drawPlanet(ctx, planets[0], absWindow, zero);
    }
    if(this.selectedObject) {

    }
    ctx.restore();
    this.objects.sort(function (o1, o2) {
        return o1.x - o2.x
    });
};

SolarMap.prototype.drawPlanet = function (ctx, planet, absWindow, zero) {
    if (planet.type === 'moon') {
        if (!planet.aphelion || this.kkmPerPixel > 400) {
            return;
        }
    }

    this.drawPlanetOrbit(ctx, planet, absWindow, zero);
    if (planet.type === 'star') {
        ctx.strokeStyle = '#ffc562';
    } else if (planet.type === 'planet') {
        ctx.strokeStyle = '#4fb169';
    } else {
        ctx.strokeStyle = '#9f45b0';
    }
    var angle = SolarTimer.angle(planet);
    var absX = Math.cos(angle) * planet.aphelion + zero.x;
    var absY = Math.sin(angle) * planet.aphelion + zero.y;


    ctx.beginPath();
    var radius = planet.meanRadius / 1000 / this.kkmPerPixel;
    if (radius < 10) {
        radius = 10;
    }
    var rel = this.getRelPoint(absX, absY);
    this.objects.push({
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
        var me = this;
        var newZero = {x: absX, y: absY};
        planet.moons.forEach(function (moon) {
            me.drawPlanet(ctx, moon, absWindow, newZero)
        })
    }
};

SolarMap.prototype.fitInScreen = function (planet) {
    return planet.type !== 'moon';
};

SolarMap.prototype.drawPlanetOrbit = function (ctx, planet, absWindow, zero) {
    var drawNormal = this.kkmPerPixel > 50 || (this.kkmPerPixel <= 50 && planet.type === 'moon');
    var smallSize = this.kkmPerPixel < 4.5;
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
        var dist = planet.aphelion / this.kkmPerPixel;
        var relP = this.getRelPoint(zero.x, zero.y);
        ctx.arc(relP.x, relP.y, dist, 0, Math.PI * 2);
        ctx.stroke();
    } else if (smallSize && points && points.length > 1) {
        ctx.beginPath();
        var p0 = this.getRelPoint(points[0].x, points[0].y);
        var p1 = this.getRelPoint(points[1].x, points[1].y);
        ctx.moveTo(p0.x, p0.y);
        ctx.lineTo(p1.x, p1.y);
        ctx.stroke();
    }
};


function drawInt(v) {
    v = parseInt(v) + '';
    var l = v.length;
    var p = v.split("");
    var pp = [];
    while (l--) {
        if (!pp[0]) {
            pp[0] = [];
        }
        if (pp[0].length === 3) {
            pp.unshift([]);
        }
        pp[0].unshift(p[l])
    }
    return pp.filter(function (v) {
        return v.length > 0
    }).map(function (value) {
        return value.join("")
    }).join(".");
}


SolarMap.prototype.unmount = function () {
    window.removeEventListener('resize', this.resizeRef);
};


SolarMap.prototype.screenSize = function () {
    return {
        h: window.innerHeight,
        w: window.innerWidth
    }
};

SolarMap.prototype.init = function () {
    var screenSize = this.screenSize();
    var min = Math.min(screenSize.w, screenSize.h);
    this.kkmPerPixel = SolarMap.MAX_RADIUS_KKM / (min / 2);
    if (screenSize.w > screenSize.h) {
        this.window.x = -(screenSize.w - screenSize.h) / 2;
        this.window.y = 0;
    } else if (screenSize.w < screenSize.h) {
        this.window.x = -(screenSize.h - screenSize.w) / 2;
        this.window.y = 0;
    } else {
        this.window.x = 0;
        this.window.y = 0;
    }
    this.window.x -= min / 2;
    this.window.y -= min / 2;

    this.resize();
};
SolarMap.prototype.changeZoom = function (zoom, relX, relY) {
    if (this.zoomStack.length > 4) {
        return;
    }
    var last = this.zoomStack.length > 1 ? this.zoomStack[this.zoomStack.length - 1] : null;
    if (last === null || (last && ((last.zoom > 1 && zoom > 1) || (last.zoom < 1 && last.zoom < 1)))) {
        this.zoomStack.push({
            absBefore: this.getAbsPoint(relX, relY),
            absZeroBefore: this.getAbsPoint(0, 0),
            zoom: zoom,
            kkm: this.kkmPerPixel,
            point: {x: relX, y: relY},
            step: 0,
            maxStep: 20
        });
    }

};


SolarMap.prototype.calculateZoom = function () {
    if (this.zoomStack.length === 0) {
        return;
    }
    var zoomInfo = this.zoomStack[0];
    if (zoomInfo.step === zoomInfo.maxStep) {
        this.zoomStack.shift();
        if (this.zoomStack.length) {
            zoomInfo = this.zoomStack[0];
            zoomInfo.absZeroBefore = this.getAbsPoint(0, 0);
            zoomInfo.kkm = this.kkmPerPixel;
        } else {
            return;
        }
    }

    zoomInfo.step++;
    if (this.kkmPerPixel > 51918 && zoomInfo.zoom > 1) {
        this.zoomStack.shift();
        this.calculateZoom();
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
    this.kkmPerPixel = zoomInfo.kkm * zoom;
    var absZeroAfter = this.getAbsPoint(0, 0);
    var dx = absZeroBefore.x - absZeroAfter.x;
    var dy = absZeroBefore.y - absZeroAfter.y;

    var zeroX = dx / this.kkmPerPixel;
    var zeroY = dy / this.kkmPerPixel;

    var windowShiftX, windowShiftY;
    dx = absBefore.x - absZeroBefore.x;
    dy = absBefore.y - absZeroBefore.y;
    if (zoomInfo.zoom < 1) {
        windowShiftX = (dx - dx * zoom) / this.kkmPerPixel;
        windowShiftY = (dy - dy * zoom) / this.kkmPerPixel;
    } else {
        windowShiftX = -(dx * (zoom - 1) / zoom) / zoomInfo.kkm;
        windowShiftY = -(dy * (zoom - 1) / zoom) / zoomInfo.kkm;
    }
    this.window.x += zeroX + windowShiftX;
    this.window.y += zeroY + windowShiftY;
};

SolarMap.prototype.resize = function () {
    var screenSize = this.screenSize();
    this.window.width = screenSize.w;
    this.window.height = screenSize.h;
    this.canvas.setAttribute('width', screenSize.w + '');
    this.canvas.setAttribute('height', screenSize.h + '');
    this.canvas.style.width = screenSize.w + 'px';
    this.canvas.style.height = screenSize.h + 'px';
};
