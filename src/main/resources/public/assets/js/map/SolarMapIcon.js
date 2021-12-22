function SolarMapIcon(folder, icon) {
    var path = "/assets/svgs/" + folder + "/" + icon + ".svg";
    var selected = "/assets/svgs/" + folder + "/" + icon + "-selected.svg";
    var me = this;
    this.icon = Dom.el('img', {src: path, onmouseenter: function(){
        me.src = selected;
        }, onmouseleave: function(){
        me.src = path;
        }});
}
