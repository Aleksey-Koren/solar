function SolarMapIcon(folder, icon) {
    var me = this;
    this.change(folder, icon);
    this.icon = Dom.el('img', {src: this.path, onmouseenter: function(){
        me.icon.src = me.selected;
        }, onmouseleave: function(){
        me.icon.src = me.path;
        }});
}
SolarMapIcon.prototype.change = function(folder, icon) {
    this.path = "/assets/svgs/" + folder + "/" + icon + ".svg";
    this.selected = "/assets/svgs/" + folder + "/" + icon + "-selected.svg";
}