/**
 *
 * @param context
 * @param params {{name: '', value: '', objectDescriptionId: int, disabled: boolean}}
 * @constructor
 */
function SocketSelector(context, params) {
    var me = this;
    this.objectDescriptionId = params.objectDescriptionId;
    if(!this.objectDescriptionId && !params.disabled) {
        throw new Error("objectDescriptionId is not defined")
    }
    this.filter = {
        alias: ''
    };
    this.selector = new GenericSelector({
        disabled: params.disabled,
        context: context,
        popupTitle: "Select Socket",
        value: params.value,
        name: params.name,
        title: "Select Socket",
        onRowClick: function(row) {
            me.selector.update((row.alias || "no alias") + " (id: " + row.id + ")", row.id);
        },
        onValueUpdate: function(value) {
            me.onValueUpdate(value);
        },
        onPopupOpen: function(){
            me.showGrid();
        },
        onPaginationChange: function(){
            me.showGrid();
        },
        columns: [
            {
                name: "id",
                title: "ID",
            },
            {
                name: "alias",
                title: "Alias",
            }
        ],
        filters: [
            Dom.el('input', {name: 'alias', placeholder: 'Alias', value: this.filter.alias, onchange: function(e){
                me.filter.alias = e.target.value.toLowerCase();
                me.selector.grid.pageInfo.page = 1;
                me.showGrid();
            }})
        ]
    });
    this.container = this.selector.container;
    this.sockets = [];
    this.dataLoaded = false;
}

SocketSelector.prototype.unmount = function() {
    this.selector.popup.hide();
};

SocketSelector.prototype.showGrid = function() {
    var me = this;
    var clb = function() {
        var grid = me.selector.grid;
        if (me.filter.alias) {
            grid.data = me.sockets.filter(function (socket) {
                return socket.alias && socket.alias.toLowerCase().indexOf(me.filter.alias) === 0;
            });
        } else {
            grid.data = me.sockets;
        }
        grid.render();
    };
    if(this.dataLoaded) {
        clb();
    } else {
        this.dataLoaded = true;
        Rest.doGet("/api/sockets/?itemDescription=" + encodeURIComponent(this.objectDescriptionId)).then(function(sockets){
            me.sockets = sockets;
            clb();
        });
    }
};

SocketSelector.prototype.onValueUpdate = function(value) {
    if(!value) {
        return;
    }
    var me = this;
    Rest.doGet("/api/sockets/" + value).then(function(object){
        me.selector.update((object.alias || "no alias") + " (id: " + object.id + ")", object.id);
    });
};
