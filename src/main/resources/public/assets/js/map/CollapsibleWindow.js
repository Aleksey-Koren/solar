function CollapsibleWindow(title, content, open) {
    var me = this;
    this.title = Dom.el('a', {href: '#', click: function(){
        if(me.open) {
            me.hide();
        } else {
            me.show();
        }
    }});
    this.body = Dom.el('div', {class: 'collapsible-title hidden'});

    this.container = Dom.el('div', {class: 'collapsible-window'}, [
        Dom.el('div', {class: 'collapsible-title'}, this.title),
        this.body
    ]);

    this.open = open || false;
    Dom.append(this.title, title);
    Dom.append(this.body, content);
    if(this.open) {
        this.show();
    }
}

CollapsibleWindow.prototype.show = function(title, content) {
    this.open = true;
    if(title === undefined) {
        Dom.removeClass(this.body, 'hidden')
    } else {
        Dom.clear(this.title);
        if(title) {
            Dom.append(this.title, title);
        }
    }
    if(content !== undefined) {
        Dom.clear(this.body);
        Dom.append(this.body, content);
    }
}
CollapsibleWindow.prototype.hide = function() {
    this.open = false;
    Dom.addClass(this.body, 'hidden');
}