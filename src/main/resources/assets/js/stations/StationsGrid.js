function StationsGrid() {

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
            {name: 'planet', title: 'Planet', render: function(row){
                return row.planet ? row.planet.title : " - ";
            }},
            {name: 'population', title: 'Population'},
            {name: 'fraction', title: 'Fraction'},
            {name: 'type', title: 'Type'},
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
    this.gridC = Dom.el('div', {}, [Dom.el('h3', 'Stations Overview'),
        Dom.el('a', {
            href: '#',
            onclick: function (e) {
                e.preventDefault();
                me.showForm('');
            }
        }, 'Create new Station'),
        this.grid]);
    Dom.append(this.container, [
        this.gridC
    ]);
}

StationsGrid.prototype.mount = function () {
    this.load();
};

StationsGrid.prototype.showForm = function (id) {
    this.container.innerHTML = '';
    var me = this;
    var form = new StationsCreationForm(id, function () {
        me.showGrid()
    });
    this.container.appendChild(form.container);
};

StationsGrid.prototype.showGrid = function () {
    this.container.innerHTML = '';
    this.container.appendChild(this.gridC);
    this.load();
};

StationsGrid.prototype.load = function () {
    var me = this;
    Rest.doGet('/api/station/?' + this.grid.queryString()).then(function (value) {
        me.grid.setPage(value);
        me.grid.render();
    }).catch(function(){
        Notification.error("Can't load stations list")
    });
};

ProductsGrid.prototype.delete = function (row) {
    var me = this;
    Rest.doDelete('/api/product/' + row.id).then(function () {
        me.load();
    });
};
