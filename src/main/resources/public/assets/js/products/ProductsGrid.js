function ProductsGrid() {
    var me = this;
    this.grid = new Grid({
        columns: [
            {
                name: 'title', title: 'Title', render: function (row) {
                    return Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            me.showForm(row.id);
                        }
                    }, 'Edit ' + row.title)
                }
            },
            {name: 'bulk', title: 'Bulk'},
            {name: 'mass', title: 'Mass'},
            {name: 'price', title: 'Price'},
            {name: 'delete', title: 'Delete', render: function(row){
                    return Dom.el('a', {
                        class: 'red',
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            if(confirm("Are you sure to delete " + row.title + "?")) {
                                me.delete(row);
                            }
                        }
                    }, 'X')
            }}
        ], onPaginationChange: function (pagination) {
            me.pagination = pagination;
            me.load();
        }
    });

    this.container = Dom.el('div');
    this.gridC = Dom.el('div', {}, [Dom.el('h3', 'Products Overview'),
        Dom.el('a', {
            href: '#',
            onclick: function (e) {
                e.preventDefault();
                me.showForm('');
            }
        }, 'Create new Product'),
        this.grid]);
    Dom.append(this.container, [
        this.gridC
    ]);
}

ProductsGrid.prototype.mount = function () {

    this.load();
};

ProductsGrid.prototype.showForm = function (id) {
    this.container.innerHTML = '';
    var me = this;
    var form = new ProductsCreationForm(id, function () {
        me.showGrid()
    });
    this.container.appendChild(form.container);
};

ProductsGrid.prototype.showGrid = function () {
    this.container.innerHTML = '';
    this.container.appendChild(this.gridC);
    this.load();
};

ProductsGrid.prototype.load = function () {
    var me = this;
    Rest.doGet('/api/product/?' + this.grid.queryString()).then(function (value) {
        me.grid.setPage(value);
        me.grid.render();
    });
};

ProductsGrid.prototype.delete = function (row) {
    var me = this;
    Rest.doDelete('/api/product/' + row.id).then(function () {
        me.load();
    });
};
