function App(container) {
    this.container = container;
    this.context = new AppContext();
    this.menu = new Menu(this.context);
    this.context.menu = this.menu;

    var me = this;
    this.menu.runApp('authManagement', function(){
        return new AuthManagement(me.context)
    });



    Dom.append(this.container, [
        this.menu.container,
        this.context.body
    ])
}

function wrapTableInput(elName, el, highlight) {
    if (!el) el = Dom.el('input', {name: elName});
    var style = {};
    if(highlight) {
        style = 'color: red';
    }
    return Dom.el('tr', {}, [
        Dom.el('td', {}, Dom.el('span', {style: style}, elName.replace(/([A-Z])/g, ' $1').replace(/^./, function(str){ return str.toUpperCase(); }))),
        Dom.el('td', {}, el)
    ]);
}