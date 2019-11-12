function ProductionGrid() {

    var me = this;
    this.product = Dom.el('select', {name: 'product'});
    this.grid = new Grid({
        columns: [
            {
                name: 'product', title: 'Product', render: function (row) {
                    var select = me.product.cloneNode(true);
                    select.onchange = function(e) {
                        row.product = e.target.value;
                    };
                    if(select.options[0]) {
                        select.value = select.options[0].value;
                    }
                    if(row.product) {
                        select.value = row.product + '';
                    }
                    return select;
                }
            },
            {name: 'power', title: 'Power', render: function(row){
                return Dom.el('input', {name: 'power', value: row.power || null, onchange: function(e){
                    row.power = e.target.value;
                }});
            }},
            {name: 'delete', title: 'Delete', render: function(row){
                return Dom.el('a', {
                    class: 'red',
                    href: '#', onclick: function (e) {
                        e.preventDefault();
                        if(confirm("Are you sure?")) {
                            me.delete(row);
                        }
                    }
                }, 'X')
            }}
        ]
    });
    this.grid.hidePagination();

    this.container = Dom.el('div');
    this.gridC = Dom.el('div', {}, [
        Dom.el('a', {
            href: '#',
            onclick: function (e) {
                e.preventDefault();
                me.grid.data.push({});
                me.grid.render();
            }
        }, 'Add Production'),
        this.grid]);
    Dom.append(this.container, [
        this.gridC
    ]);

    this.loadDropdown()
}

ProductionGrid.prototype.loadDropdown = function () {
    var me = this;
    return Rest.doGet('/api/product/utils/dropdown').then(function(options){
        me.product.innerHTML = '';
        Dom.append(me.product, options.map(function(opt){
            return Dom.el('option', {value: opt.value}, opt.label);
        }));
        me.grid.render();
    }).catch(function(){
        Notification.error("Can't load planets dropdown")
    });
};

ProductionGrid.prototype.getData = function () {
    return this.grid.data;
};

ProductionGrid.prototype.delete = function (row) {
    var l = this.grid.data.length;
    while(l--) {
        if(this.grid.data[l] === row) {
            this.grid.data.splice(l, 1);
            break;
        }
    }
    this.grid.render();
};



