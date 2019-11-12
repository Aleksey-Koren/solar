function UserForm(context, user, openGrid) {
    var w = wrapTableInput;
    var me = this;
    this.grid = this.createUserPermissions();
    this.form = Dom.el('table', {}, [
        w('title', Dom.el('input', {name: 'title', value: user.title})),
        w('permissions', this.grid.container),
        w(' ', Dom.el('div', {}, [
            Dom.el('input', {
                type: 'button', value: 'Back', onclick: function () {
                    openGrid();
                }
            }),
            ' ',
            Dom.el('input', {type: 'submit', value: 'Save'})
        ]))
    ]);
    this.container = Dom.el('form', {
        submit: function (e) {
            e.preventDefault();
            var form = Dom.fromForm(me.form);
            form.id = user.id;

            Rest.doPost('/api/users/', form).then(function () {
                openGrid();
            }).catch(function () {
                Notification.error("Can't save user, server error");
            })
        }
    }, this.form);
    this.context = context;
    var store = this.context.stores.permissions;
    this.user = user;
    this.userPermissions = [];
    this.loadUserPermissions();
    this.store = store;
    store.listen(this);
    if (store.isLoaded) {
        this.updateDropdown(store);
    } else {
        store.update();
    }
}

UserForm.prototype.updateDropdown = function (store) {
    this.permissionsDropdown.innerHTML = '';
    Dom.append(this.permissionsDropdown, Dom.el('option', {value: ''}, ''));
    var map = {};
    this.userPermissions.forEach(function (permission) {
        map[permission.permissionTypeId] = permission;
    });
    Dom.append(this.permissionsDropdown, store.dropdown.filter(function (option) {
        return !map[option.value];
    }).map(function (opt) {
        return Dom.el('option', {value: opt.value}, opt.label);
    }));
};

UserForm.prototype.onStoreChange = function (data) {
    this.updateDropdown(data);
};
UserForm.prototype.createUserPermissions = function () {
    var me = this;
    var permissionsDropdown = Dom.el('select', {
        onchange: function (e) {
            var value = parseInt(e.target.value);
            if (!isNaN(value)) {
                var row = {
                    id: null,
                    userId: me.user.id,
                    permissionTypeId: value,
                    title: permissionsDropdown.options[permissionsDropdown.selectedIndex].innerText
                };
                me.userPermissions.push(row);
                grid.data.push(row);
                grid.render();
                me.updateDropdown(me.store);
                Rest.doPost('/api/permissions/elevate', row).then(function(response){
                    row.id = response.id;
                }).catch(function () {
                    Notification.error("Can't elevate permission, server error");
                });
            }
            permissionsDropdown.value = '';
        }
    });


    var grid = new Grid({
        columns: [{
            name: 'permissions', headerRender: function () {
                return permissionsDropdown;
            }, render: function (row) {
                return Dom.el('span', {}, [
                    row.title,
                    Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            if (!row.id || confirm("Are you sure to delete " + row.title + "?")) {
                                function removeRow() {
                                    grid.data = grid.data.filter(function (r) {
                                        return row !== r;
                                    });
                                    grid.render();
                                    me.userPermissions = me.userPermissions.filter(function (r) {
                                        return row !== r;
                                    });
                                    me.updateDropdown(me.store);
                                }

                                if (!row.id) {
                                    removeRow();
                                } else {
                                    row.remove = true;
                                    Rest.doPost('/api/permissions/elevate', row).then(function () {
                                        removeRow();
                                    }).catch(function () {
                                        Notification.error("Can't delete permission, server error");
                                    });
                                }
                            }
                        }
                    }, 'X')
                ]);
            }
        }], data: []
    });
    grid.hidePagination();
    this.permissionsDropdown = permissionsDropdown;
    return grid;
};

UserForm.prototype.submit = function () {
    this.store.remove(this);
};
UserForm.prototype.back = function () {
    this.store.remove(this);
};

UserForm.prototype.loadUserPermissions = function () {
    if (this.user.id) {
        var me = this;
        Rest.doGet('/api/permissions/user/' + this.user.id).then(function (response) {
            me.userPermissions = response;
            me.grid.data = response;
            me.grid.render();
            me.updateDropdown(me.store);
        }).catch(function () {
            Notification.error("Can't load user permissions, server error");
        });
    }
};
