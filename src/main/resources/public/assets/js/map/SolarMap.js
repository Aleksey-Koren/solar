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
            var obj = me.overObject(me.objects[i], e)
            if(obj) {
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
    me.objectWindow = null;
    this.canvas.addEventListener('click', function(e){
        var obj = me.overObject(me.selectedObject, e);
        if(obj) {
            if(!me.objectWindow) {
                me.objectWindow = new PlanetWindow(me.context);
            }
            me.objectWindow.show(obj.obj, false);
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

SolarMap.prototype.overObject = function (obj, e) {
    if(!obj) {
        return null;
    }
    if(obj.type === 'moon' || obj.type === 'planet' || obj.type === 'sun') {
        var length = Math.sqrt(Math.pow(obj.x - e.clientX, 2) + Math.pow(obj.y - e.clientY, 2));
        if(length <= obj.r) {
            return obj;
        }
    } else if(obj.x - obj.r <= e.clientX && obj.x + obj.r >= e.clientX && obj.y - obj.r <= e.clientY && obj.y + obj.r >= e.clientY) {
        return obj;
    }
    return null;
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
    this.drawKmPerPixel();
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
    ctx.restore();
    this.objects.sort(function (o1, o2) {
        return o1.x - o2.x
    });
};

SolarMap.prototype.drawKmPerPixel = function () {
    var ctx = this.ctx;
    if(this.kkmPerPixel > 1.5) {
        ctx.strokeText("KM per pixel: " + Math.floor(this.kkmPerPixel * 1000), 10, 130, {color: '#4fb169'});
    } else if(this.kkmPerPixel > 0.001) {
        ctx.strokeText("KM per pixel: " + Math.floor(this.kkmPerPixel * 10000) / 10, 10, 130, {color: '#4fb169'});
    } else {
        ctx.strokeText("M per pixel: " + Math.floor(this.kkmPerPixel * 1000000) , 10, 130, {color: '#4fb169'});
    }
}
SolarMap.prototype.drawPlanet = function (ctx, planet, absWindow, zero) {
    SolarMapPlanetRender.drawPlanet(this, ctx, planet, absWindow, zero);
};

SolarMap.prototype.fitInScreen = function (planet) {
    return planet.type !== 'moon';
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
    SolarMapZoom.changeZoom(this, zoom, relX, relY);
};


SolarMap.prototype.calculateZoom = function () {
    SolarMapZoom.calculateZoom(this);
};

SolarMap.prototype.resize = function () {
    var screenSize = this.screenSize();
    var dpi = window.devicePixelRatio;
    this.window.width = screenSize.w;
    this.window.height = screenSize.h;
    this.canvas.setAttribute('width', (screenSize.w  * dpi) + '');
    this.canvas.setAttribute('height', (screenSize.h  * dpi) + '');
    //this.canvas.style.width = screenSize.w + 'px';
    //this.canvas.style.height = screenSize.h + 'px';
};
