/**
 *
 * @param params {{data:[], columns:[], onPaginationChange: Function}}
 * @constructor
 */
function Grid(params) {
    this.data = params.data || [];
    this.columns = params.columns;
    this.pageInfo = {page: 1, size: 10, total: 0};
    this.table = Dom.el('table', 'grid');
    this.pagination = new GridPagination(this.pageInfo, params.onPaginationChange, this.data);
    this.container = Dom.el('div', {}, [
        params.controls || null,
        this.table,
        this.pagination.container
    ].filter(function(v){return v}));
    this.render();
}

Grid.prototype.setPage = function(page) {
    this.data = page.content;
    this.pageInfo.total = page.totalElements;
    this.pagination.update(this.data);
};
Grid.prototype.queryString = function() {
    return "page=" + encodeURIComponent(this.pageInfo.page - 1) + "&size=" + encodeURIComponent(this.pageInfo.size);
};

Grid.prototype.render = function() {
    var header = this.renderHeader();
    var body = this.renderBody();
    this.table.innerHTML = '';
    Dom.append(this.table, [header, body]);
};

Grid.prototype.renderHeader = function() {
    var out = Dom.el('tr');
    this.columns.forEach(function(col) {
        var header = col.headerRender ? col.headerRender() : (col.title || "");
        Dom.append(out, Dom.el('th', {}, header));
    });
    return Dom.el('thead', {}, out);
};


Grid.prototype.hidePagination = function() {
    this.pagination.hide();
};

Grid.prototype.renderBody = function() {
    var me = this;
    var out = [];
    var i = 0;
    this.data.forEach(function(row) {
       out.push(Dom.el('tr', {class: i%2 ? 'even' : 'odd'}, me.columns.map(function (column) {
           var content;
           if(column.render) {
               content = column.render(row);
           } else {
               content = row[column.name];
           }
           return Dom.el('td', {}, content === undefined ? "" : content);
       })));
    });
    return Dom.el('tbody', {}, out);
};


function GridPagination(pagination, onChange, data) {
    var me = this;
    this.pagination = pagination;
    this.page = Dom.el('input', {
        value: pagination.page,
        class: 'pagination-page',
        onchange: function () {
            pagination.page = me.page.value;
            onChange(pagination);
        }
    });
    this.total = Dom.el('span',{}, this.prepareTotal(data));
    this.pageSize = Dom.el('select', {onchange: function(){
        pagination.page = 1;
        pagination.size = parseInt(me.pageSize.value);
        onChange(pagination)
    }}, [
        Dom.el('option', {value: 10}, 10),
        Dom.el('option', {value: 20}, 20),
        Dom.el('option', {value: 50}, 50),
        Dom.el('option', {value: 100}, 100),
        Dom.el('option', {value: 500}, 500)
    ]);
    this.container = Dom.el('div', {}, [
        Dom.el('span', {}, 'Page '),
        " ",
        this.page,
        " ",
        Dom.el('span', {}, 'Page Size: '),
        " ",
        this.pageSize,
        " ",
        this.total
    ]);
}

GridPagination.prototype.prepareTotal = function(data) {
    var page = (this.pagination.page || 1) - 1;
    var total = this.pagination.total;
    if(total === 0) {
        return "";
    }
    return ["Shown ", page * (this.pagination.size) + 1, " - ", page * this.pagination.size + data.length, "from",  total + ""].join(' ');

};
GridPagination.prototype.update = function(data) {
    this.total.innerHTML = this.prepareTotal(data);
    this.page.value = this.pagination.page || 1;
    this.pageSize.value = this.pagination.size;
};
GridPagination.prototype.hide = function() {
    Dom.addClass(this.container, "hidden");
};