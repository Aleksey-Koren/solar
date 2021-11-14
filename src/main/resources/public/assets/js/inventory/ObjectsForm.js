function ObjectsForm(data, context, showGrid, params) {
    this.context = context;
    this.planetsSelector = null;
    this.objectDescriptionSelector = null;
    this.userSelector = null;
    this.showGrid = showGrid;
    this.form = this.createForm(data, params);
    this.socketList = data.socketList;
    if(data.socketList && data.attachedObjects) {
        var socketMap = {};
        data.socketList.forEach(function(socket){
            socketMap[socket.id] = socket;
        });
        data.attachedObjects.forEach(function(object){
            if(socketMap[object.attachedToSocket]) {
                socketMap[object.attachedToSocket].item = object;
            }
        })
    }
    this.sockets = new SocketsGrid(context, data.socketList, this.form);
    this.container = Dom.el('div', {class: 'objects-form'}, [
        this.form,
        this.sockets
    ]);
    if (data) {
        Dom.form(this.form, data);
    }
}

ObjectsForm.prototype.createForm = function (data, params) {
    var w = wrapTableInput;
    this.planetsSelector = new PlanetsSelector(this.context, {name: 'planet'});
    this.objectDescriptionSelector = new ObjectDescriptionSelector(this.context, {
        name: 'hullId',
        itemType: params.itemType,
        onObjectSelect: function(obj) {
            var f = Dom.fromForm(me.form);
            if(!f.durability) {
                f.durability = obj.durability;
                Dom.form(me.form, f);
            }
        }
    });
    this.userSelector = new UserSelector(this.context, {name: 'userId'});
    this.shipSelector = new ObjectSelector(this.context, {
        name: 'attachedToShip',
        disabled: params.attachedToShipDisabled
    });
    this.socketSelector = new SocketSelector(this.context, {
        name: 'attachedToSocket',
        objectDescriptionId: params.socketDescription,
        disabled: params.attachedToSocketDisabled
    });
    var me = this;
    var disablePlanet = params.disablePlanet;
    var isItem = params.isItem;
    return Dom.el('form', {
        submit: function (e) {
            e.preventDefault();
            var object = Dom.fromForm(me.form);
            object.attachedObjects = me.socketList ? me.socketList.filter(function(socket){
                return socket.item;
            }).map(function(socket){
                return socket.item;
            }) : [];
            Rest.doPost("/api/objects/config", object).then(function (object) {
                me.showGrid(object);
            }).catch(function () {
                Notification.error("Can't save object");
            });
        }
    }, [
        Dom.el('input', {type: 'hidden', name: 'id'}),
        disablePlanet ? null : w("planet", this.planetsSelector.container),//planet
        isItem ? null : w("population"),
        isItem ? null : w("fraction"),
        w("title"),
        disablePlanet ? null : w("x"),
        disablePlanet ? null : w("y"),
        disablePlanet ? null : w("aphelion"),
        disablePlanet ? null : w("orbitalPeriod"),
        disablePlanet ? null : w("angle"),
        w("Hull", this.objectDescriptionSelector.container),
        w("User", this.userSelector.container),//user_id
        w("Is Active", Dom.el('input', {type: 'checkbox', name: 'active'})),
        w("durability"),
        w("Attached to Ship", this.shipSelector.container),
        w("Attached to Socket", this.socketSelector.container),
        w("Status", Dom.el('select', {name: 'status'}, [
            Dom.el('option', {value: ''}, ' '),
            Dom.el('option', {value: 'in_space'}, 'In Space'),
            Dom.el('option', {value: 'attached_to'}, 'Attached To Object'),
            Dom.el('option', {value: 'in_container'}, 'In Container'),
        ])),
        w(' ', [
            Dom.el('input', {
                type: 'button', value: "Back", onclick: function (e) {
                    me.showGrid(null)
                }
            }),
            ' ',
            Dom.el('input', {type: 'submit', value: "Save"})
        ])
    ]);
};

ObjectsForm.prototype.unmount = function () {
    this.planetsSelector.unmount();
    this.objectDescriptionSelector.unmount();
    this.userSelector.unmount();
    this.shipSelector.unmount();
    this.socketSelector.unmount();
};


function SocketsGrid(context, sockets, parentForm) {
    sockets = sockets || [];
    this.parentForm = parentForm;
    var me = this;
    this.context = context;
    this.selectedSocket = null;
    this.selector = new GenericSelector({
        context: context,
        popupTitle: "Select Object",
        value: "",
        name: "",
        title: "",
        onRowClick: function (row) {
            me.onSelectObject(row);
        },
        onValueUpdate: function () {
        },
        onPopupOpen: function () {
            me.openPage();
        },
        onPaginationChange: function () {
            me.openPage();
        },
        filters: [
            Dom.el('a', {
                href: '#', click: function (e) {
                    e.preventDefault();
                    me.showCreateObject();
                }
            }, 'Create New')
        ],
        columns: [{
            name: 'id',
            title: "ID"
        }, {
            name: "title",
            title: "Title",
            render: function(row, con){
                if(row.title) {
                    return row.title;
                }
                if(!con.titles) {
                    con.titles = {};
                    context.stores.inventory.inventoryItems.forEach(function(item){
                        con.titles[item.id] = item.title;
                    })
                }
                return con.titles[row.hullId] || "-"
            }
        }]
    });

    this.container = Dom.el('table', {class: 'sockets-container'});
    this.sockets = sockets;
    this.renderTable();
}


SocketsGrid.prototype.renderTable = function () {
    var me = this;
    var rows = [];
    var row = null;
    var titles = {};
    this.context.stores.inventory.inventoryItems.forEach(function(item){
        titles[item.id] = item.title;
    });

    this.sockets.forEach(function (socket, i) {
        if (i % 6 === 0) {
            row = Dom.el('tr');
            rows.push(row);
        }
        const alias = socket.alias || "";
        row.appendChild(Dom.el('td', {
            click: function () {
                me.showSelector(socket);
            }, class: 'socket-item'
        }, [
            "Alias: " + alias, Dom.el('br'),
            "Item: " + (socket.item ? socket.item.title || titles[socket.item.hullId] : "none")
        ]));
    });
    if (row) {
        while (row.childNodes.length < 6) {
            row.appendChild(Dom.el('td'));
        }
    }
    Dom.clear(this.container);
    Dom.append(this.container, rows);
};

SocketsGrid.prototype.showSelector = function (socket) {
    this.selectedSocket = socket;
    this.openPage();
};
SocketsGrid.prototype.showCreateObject = function () {

    this.selector.popup.hide();
    var me = this;
    var parent = Dom.fromForm(this.parentForm);
    var popup = new Popup({
        context: this.context,
        title: "Create new Object",
        content: new ObjectsForm({
                userId: parent.userId,
                active: true,
                attachedToShip: parent.id,
                attachedToSocket: this.selectedSocket.id,
                status: 'attached_to'
            }, this.context,
            function (object) {
                if (object) {
                    me.onSelectObject(object);
                }
                popup.hide();
            },
            {
                itemType: this.selectedSocket.itemTypeId,
                attachedToShipDisabled: true,
                socketDescription: this.selectedSocket.itemTypeId,
                attachedToSocketDisabled: true,
                disablePlanet: true,
                isItem: true,
            })
    });
    popup.show();
};
SocketsGrid.prototype.openPage = function () {
    var me = this;
    Rest.doGet("/api/objects/config?" +
        this.selector.grid.queryString() +
        "&detached=true&inventoryType=" + this.selectedSocket.itemTypeId
    ).then(function (page) {
        me.selector.popup.show();
        me.selector.grid.setPage(page);
        me.selector.grid.render();
    });
};
SocketsGrid.prototype.onSelectObject = function (object) {
    this.selectedSocket.item = object;
    object.attachedToSocket = this.selectedSocket.id;
    this.selectedSocket = null;
    this.renderTable();
};
