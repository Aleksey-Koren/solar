/**
 * @param params {{
 * context: object,
 * disabled: boolean,
 * popupTitle: object,
 * value: '', name: '', columns: [], class: '',
 * title: '', class: '',
 * filters: [],
 * onRowClick: function,
 * onPopupOpen: function,
 * onValueUpdate: function,
 * onPaginationChange: function}}
 * @constructor
 */
function GenericSelector(params) {
    var me = this;
    this.onRowClick = params.onRowClick;
    this.onPopupOpen = params.onPopupOpen;
    this.onValueUpdate = params.onValueUpdate;
    this.defaultTitle = params.title || "Click to Select";
    this.input = Dom.el('input', {
        type: 'hidden',
        name: params.name,
        value: params.value !== undefined && params.value !== null ? params.value : ''
    });
    this.input.updateComponent = function (v) {
        me.updateComponent(v)
    };
    this.link = Dom.el('a', {
        href: '#', class: params.disabled ? 'disabled' : '', click: function (e) {
            e.preventDefault();
            if(params.disabled) {
                return;
            }
            me.popup.show();
            me.onPopupOpen();
        }
    }, this.defaultTitle);
    this.grid = new Grid({
        data: [],
        columns: params.columns,
        onRowClick: function (row) {
            me.onRowClick(row);
            me.popup.hide();
        },
        onPaginationChange: params.onPaginationChange
    });
    this.grid.container.className = "object-selection-grid";

    this.popup = new Popup({
        context: params.context,
        title: params.popupTitle,
        content: [
            params.filters,
            this.grid.container
        ]
    });

    this.clear = params.disabled ? null : Dom.el('a', {href: '#', class: 'hidden', click: function(e){
        e.preventDefault();
        if(params.disabled) {
            return;
        }
        me.updateComponent('');
    }}, 'X');

    this.container = Dom.el('div',
        {class: 'object-selector' + (params.class ? params.class : '')},
        [
            this.input,
            this.link,
            ' ',
            this.clear
        ]
    )
}

GenericSelector.prototype.update = function (title, value) {
    Dom.clear(this.link);
    Dom.append(this.link, title);
    this.input.value = value;
    if(this.clear) {
        if (value) {
            this.clear.className = 'delete-link';
        } else {
            this.clear.className = 'hidden';
        }
    }
};
GenericSelector.prototype.updateComponent = function (value) {
    this.input.value = value;
    if(!value) {
        Dom.clear(this.link);
        Dom.append(this.link, this.defaultTitle);
        if(this.clear) {
            this.clear.className = 'hidden';
        }
    } else {
        if(this.clear) {
            this.clear.className = 'delete-link';
        }
    }
    this.onValueUpdate(value);
};