/**
 *
 * @param params {{data:[], columns:[], onPaginationChange: Function}}
 * @constructor
 */
function Grid(params) {
    this.data = params.data || [];
    this.onRowClick = params.onRowClick;
    this.columns = params.columns;
    this.pageInfo = {page: 1, size: 10, total: 0};
    this.table = Dom.el('table', 'grid');
    this.pagination = new GridPagination(this.pageInfo, params.onPaginationChange, this.data);
    this.container = Dom.el('div', {}, [
        params.controls || null,
        this.table,
        this.pagination.container
    ].filter(function (v) {
        return v
    }));
    this.render();
}

Grid.prototype.setPage = function (page) {
    this.data = page.content;
    this.pageInfo.total = page.totalElements;
    this.pagination.update(this.data);
};
Grid.prototype.queryString = function () {
    return "page=" + encodeURIComponent(this.pageInfo.page - 1) + "&size=" + encodeURIComponent(this.pageInfo.size);
};

Grid.prototype.render = function (data) {
    if (data) {
        this.data = data;
    }
    var header = this.renderHeader();
    var body = this.renderBody();
    Dom.clear(this.table);
    Dom.append(this.table, [header, body]);
};

Grid.prototype.renderHeader = function () {
    var out = Dom.el('tr');
    this.columns.forEach(function (col) {
        var header = col.headerRender ? col.headerRender() : (col.title || "");
        Dom.append(out, Dom.el('th', {}, header));
    });
    return Dom.el('thead', {}, out);
};


Grid.prototype.hidePagination = function () {
    this.pagination.hide();
};

Grid.prototype.renderBody = function () {
    var me = this;
    var out = [];
    this.data.forEach(function (row, i) {
        var params = {class: i % 2 ? 'even' : 'odd'};
        if(me.onRowClick) {
            params.click = function() {
                me.onRowClick(row);
            }
        }
        var cache = {};
        out.push(Dom.el('tr', params, me.columns.map(function (column) {
            var content;
            if (column.render) {
                content = column.render(row, cache);
            } else {
                content = row[column.name];
            }
            return Dom.el('td', {}, content === undefined ? "" : content);
        })));
    });
    if(this.data.length === 0) {
        out.push(Dom.el('tr', {colspan: me.columns.length}, "No Content"));
    }
    return Dom.el('tbody', {}, out);
};


function GridPagination(pagination, onChange, data) {
    var me = this;
    this.pagination = pagination;
    this.page = Dom.el('input', {
        value: pagination.page,
        class: 'pagination-page',
        onkeyup: function () {
            if(me.page.value) {
                pagination.page = me.page.value;
                onChange(pagination);
            }
        }
    });
    this.total = Dom.el('span', {}, this.prepareTotal(data));
    this.pageSize = Dom.el('select', {
        onchange: function () {
            pagination.page = 1;
            pagination.size = parseInt(me.pageSize.value);
            onChange(pagination)
        }
    }, [
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

GridPagination.prototype.prepareTotal = function (data) {
    var page = (this.pagination.page || 1) - 1;
    var total = this.pagination.total;
    if (total === 0) {
        return "";
    }
    return ["Shown ", page * (this.pagination.size) + 1, " - ", page * this.pagination.size + data.length, "from", total + ""].join(' ');

};
GridPagination.prototype.update = function (data) {
    this.total.innerHTML = this.prepareTotal(data);
    this.page.value = this.pagination.page || 1;
    this.pageSize.value = this.pagination.size;
};
GridPagination.prototype.hide = function () {
    Dom.addClass(this.container, "hidden");
};


/**
 *
 * @param params {{
 *     context: object,
 *     content: object,
 *     title?: object,
 *     onClose?: () => void,
 *     noControls?: boolean
 * }}
 * @constructor
 */
function Popup(params) {
    this.context = params.context;
    this.content = Dom.el('div', {}, params.content ? params.content : undefined);
    var me = this;
    if(params.title) {
        this.title = Dom.el('h3', {class: "popup-title"}, params.title);
    } else {
        this.title = null;
    }
    this.window = Dom.el('div', {class: "modal"}, [
        this.title,
        this.content,
        params.noControls ? null : Dom.el('div', {}, [Dom.el('div', {
            class: 'popup-close',
            onclick: function () {
                me.hide();
            }
        })])
    ]);
    this.onClose = params.onClose;
    this.hideWrapper = null;
}
Popup.overlay = {
    components: [],
    dom: Dom.el(
        'div',
        {class: "overlay"}
    )
}
Popup.prototype.setContent = function (content) {
    Dom.append(this.content, content)
};
Popup.prototype.show = function () {
    if(this.context && this.context.escListener) {
        var me = this;
        this.hideWrapper = function(){
            me.hide();
        };
        this.context.escListener.add(this.hideWrapper)
    }
    document.body.appendChild(this.window);
    if(Popup.overlay.components.indexOf(this) === -1) {
        Popup.overlay.components.push(this);
        document.body.appendChild(Popup.overlay.dom);
    }
    Popup.overlay.dom.style.zIndex = (1100 + (Popup.overlay.components.length * 2)) + "";
    this.window.style.zIndex = (1101 + (Popup.overlay.components.length * 2)) + "";
};
Popup.prototype.hide = function () {
    if(this.context && this.context.escListener) {
        this.context.escListener.remove(this.hideWrapper);
    }
    this.window.parentElement && this.window.parentElement.removeChild(this.window);
    try {
        this.onClose && this.onClose();
    } catch (e) {
        console.error("fail to execute popup callback", e);
    }
    var index = Popup.overlay.components.indexOf(this);
    if(index > -1) {
        Popup.overlay.components.splice(index, 1);
    }
    if(Popup.overlay.components.length === 0) {
        Popup.overlay.dom.parentElement && Popup.overlay.dom.parentElement.removeChild(Popup.overlay.dom);
    }
    Popup.overlay.dom.style.zIndex = (1100 + (Popup.overlay.components.length * 2)) + "";
};

/**
 *
 * @param params {{
 *     context: object,
 *     content: object,
 *     onClose: (boolean) => void,
 *     title: object,
 *     noButton?: any,
 *     yesButton?: any,
 *     noControls?: boolean
 * }}
 * @constructor
 */
function Confirm(params) {
    this.closed = false;
    var me = this;
    this.onClose = params.onClose;
    this.popup = new Popup({
        context: params.context,
        onClose: function() {
            if(!me.closed) {
                me.closed = true;
                params.onClose(false);
            }
        },
        content: Dom.el('div', null, [
            params.content,
            Dom.el('div', null, [
                Dom.el('button', {onclick: function() {
                    me.hide(false);
                }}, params.noButton || 'No'),
                Dom.el('button', {onclick: function() {
                    me.hide(true);
                }}, params.yesButton || 'Yes')
            ])
        ])
    })
    this.container = this.popup.container;
}

Confirm.prototype.hide = function(value) {
    this.closed = true;
    this.popup.hide();
    this.onClose(value || false)
}

Confirm.prototype.show = function() {
    this.closed = false;
    this.popup.show();
}