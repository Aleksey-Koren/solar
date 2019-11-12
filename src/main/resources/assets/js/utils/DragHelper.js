function DragHelper () {
    this.start = false;
    this.probablyStart = false;
    this.ix = -1;
    this.iy = -1;
    this.cx = -1;
    this.cy = -1
}

DragHelper.prototype.get = function() {
    return {
        x: (this.start ?  this.cx - this.ix : 0),
        y: (this.start ?  this.cx - this.ix : 0)
    }
};


DragHelper.prototype.listen = function(el, mouseMove, dragStop) {
    var me = this;
    el.addEventListener('mousedown', function (e) {
        me.probablyStart = true;
        me.ix = e.clientX;
        me.iy = e.clientY;
        me.cx = e.clientY;
        me.cy = e.clientY;
    });
    el.addEventListener('mousemove', function (e) {
        if(me.probablyStart) {
            if (Math.abs(me.ix - e.clientX) > 5 || Math.abs(me.iy - e.clientY) > 5) {
                me.probablyStart = false;
                me.start = true;
            }
        }
        if(me.start) {
            me.cx = e.clientX;
            me.cy = e.clientY;
            mouseMove(me.cx - me.ix, me.cy - me.iy)
        }
    });
    el.addEventListener('mouseup', function () {
        if(me.start) {
            dragStop(me.cx - me.ix, me.cy - me.iy);
            me.start = false;
        }
        if(me.probablyStart) {
            me.probablyStart = false;
        }
    });
};