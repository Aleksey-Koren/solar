function EscListener() {
    this.bus = [];
    var me = this;
    document.addEventListener("keyup", function(e){
        if(e.key === 'Escape' && me.bus.length) {
            me.bus.pop()();
        }
    })
}

EscListener.prototype.add = function(clb) {
    this.bus.push(clb);
    if(this.bus.length > 10) {
        console.warn("too much event listeners in esc listener")
    }
    console.log('esc listeners (add): ' + this.bus.length);
};
EscListener.prototype.remove = function(clb) {
    var l = this.bus.length;
    while(l--) {
        if(this.bus[l] === clb) {
            this.bus.splice(l, 1);
        }
    }
    console.log('esc listeners (remove): ' + this.bus.length);
};

