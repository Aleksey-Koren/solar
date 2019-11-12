function UserGrid(openForm) {
    var me = this;
    this.grid = new Grid({
        data: [], columns: [
            {
                name: 'title', title: "User", render: function (row) {
                    return Dom.el('a', {
                        href: '#', onclick: function (e) {
                            e.preventDefault();
                            openForm(row);
                        }
                    }, 'Edit ' + (row.title || row.login))
                }
            }
        ], onPaginationChange: function () {
            me.onPaginationChange();
        }
    });
    this.onPaginationChange(this.grid.pageInfo);
    this.container = this.grid.container;
}

UserGrid.prototype.onPaginationChange = function(pagination) {
    var me = this;
    Rest.doGet('/api/users/?' + this.grid.queryString()).then(function(response){
        me.grid.data = response;
        me.grid.render();
    }).catch(function(){
        Notification.error("Could not load user list, server error");
    });
};