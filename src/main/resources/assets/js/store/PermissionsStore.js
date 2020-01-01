function PermissionsStore() {
    AbstractStore.call(this);
    this.permissionTypes = [];
    this.dropdown = [];
    this.name = "permissions"
}
PermissionsStore.prototype = Object.create(AbstractStore.prototype);


PermissionsStore.prototype.update = function() {
    var me = this;
    Rest.doGet('/api/permissions').then(function (value) {
        me.isLoaded = true;
        me.permissionTypes = value;
        me.dropdown = [];
        for (var i = 0; i < value.length; i++) {
            var p = value[i];
            me.dropdown.push({value: p.id, label: p.title});
        }
        me.notify({
            dropdown: me.dropdown,
            permissionTypes: me.permissionTypes
        });
    });
};
