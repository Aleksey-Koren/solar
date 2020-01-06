function ItemSocketGrid(context) {
    var me = this;
    this.context = context;

    this.dropdown = Dom.el('select', {onchange: function(e){
        var count = parseInt(me.amount.value);
        count = isNaN(count) ? 1 : count;
        while(count > 0) {
            me.addSocket(e.target.value);
            count--;
        }
        me.dropdown.value = '';
    }});
    this.amount = Dom.el('input', {value: 1});
    this.grid = new Grid({
        data: [],
        columns: [
            {
                name: 'title',
                title: 'title',
                render: function(row) {
                    return row.title;
                }
            },
            {
                name: 'sort',
                title: ' ',
                render: function(row) {
                    var up = Dom.el('a', {href: '#', onclick: function(){
                            me.move(row, true);
                        }}, 'UP');
                    var down = Dom.el('a', {href: '#', onclick: function(){
                            me.move(row, false);
                        }}, 'DOWN');
                    var content =  [];
                    if(me.grid.data[0] === row) {
                        content.push(down);
                    } else if(me.grid.data[me.grid.data.length - 1] === row) {
                        content.push(up);
                    } else {
                        content.push(up);
                        content.push(" ");
                        content.push(down);
                    }
                    return Dom.el('div', {}, content);
                }
            },
            {
                name: 'delete',
                title: 'Delete',
                render: function(row){
                    return Dom.el('a', {href: '#', onclick: function(e){
                        e.preventDefault();
                        me.grid.data.splice(me.grid.data.indexOf(row), 1);
                        me.grid.render();
                    }}, 'X')
                }
            }
        ]
    });
    this.grid.hidePagination();

    this.container = Dom.el('div', {}, [
        Dom.el('div', {}, "Sockets"),
        this.dropdown,
        ' ',
        this.amount,
        this.grid.container
    ]);
    var store = this.context.stores.inventory;
    store.listen(this);
    if(store.isTypesLoaded) {
        this.updateDropdown();
    } else {
        store.update('type');
    }
}

ItemSocketGrid.prototype.move = function(row, up) {
    var position = this.grid.data.indexOf(row);
    if(up) {
        if(position === 0) {
            return;
        }
        this.grid.data[position] = this.grid.data[position - 1];
        this.grid.data[position - 1] = row;
    } else {
        if(position === this.grid.data.length - 1) {
            return;
        }
        this.grid.data[position] = this.grid.data[position + 1];
        this.grid.data[position + 1] = row;
    }
    this.grid.render();
};
ItemSocketGrid.prototype.addSocket = function(id) {
    id = parseInt(id);
    if(id && !isNaN(id)) {
        var list = this.context.stores.inventory.inventoryTypes;
        for(var i = 0; i < list.length; i++) {
            var item = list[i];
            if(item.id === id) {
                this.grid.data.push(item);
                this.grid.render();
                break;
            }
        }
    }
};

ItemSocketGrid.prototype.onStoreChange = function() {
    this.updateDropdown();
};

ItemSocketGrid.prototype.unmount = function() {
    this.context.stores.inventory.remove(this);
};

ItemSocketGrid.prototype.setData = function(data) {
    this.grid.data = data;
    this.grid.render();
};

ItemSocketGrid.prototype.updateDropdown = function() {
    this.dropdown.innerHTML = '';
    var me = this;
    var map = {};
    me.grid.data.forEach(function(row){
        map[row.id] = true;
    });
    me.dropdown.appendChild(Dom.el('option', {value: ''}, ''));
    this.context.stores.inventory.inventoryTypes.forEach(function(mod) {
        me.dropdown.appendChild(Dom.el('option', {value: mod.id}, mod.title))
    })
};