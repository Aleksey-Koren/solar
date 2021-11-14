function PermissionsManagement(context) {
    this.context = context;
    this.container = Dom.el('div');

}

PermissionsManagement.prototype.render = function() {
    this.container.innerHTML = '';
    var grid = new PermissionTypesGrid(this.context);
    this.container.appendChild(grid.container);
};
