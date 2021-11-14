function InventoryManagement(context) {
    this.context = context;
    this.content = Dom.el('div');
    this.activeInstance = null;
    this.app = '';
    var me = this;
    this.container = Dom.el('div', {}, [
        Dom.el('div', 'head-menu', [
            Dom.el('a', {href: '#', onclick: function(e) {
                e.preventDefault();
                me.mount('types', new ObjectsGrid(context));
            }}, 'Objects Editor'),
            Dom.el('a', {href: '#', onclick: function(e) {
                e.preventDefault();
                me.mount('types', new InventoryTypeGrid(context));
            }}, 'Inventory Types'),
            Dom.el('a', {href: '#', onclick: function(e) {
                e.preventDefault();
                me.mount('items', new InventoryItemGrid(context));
            }}, 'Inventory Descriptions'),
            Dom.el('a', {href: '#', onclick: function(e) {
                e.preventDefault();
                me.mount('modifications', new InventoryModificationsGrid(context));
            }}, 'Inventory Modifications'),

        ]),
        this.content
    ]);
}

InventoryManagement.prototype.mount = function (name, instance) {
    if(this.activeInstance) {
        if( this.activeInstance.unmount) {
            this.activeInstance.unmount();
        } else {
            console.warn("no unmount function in ", this.activeInstance);
        }
    }
    if(this.app !== name) {
        this.content.innerHTML = '';
        this.content.appendChild(instance.container);
    }
};

InventoryManagement.prototype.render = function () {

};

