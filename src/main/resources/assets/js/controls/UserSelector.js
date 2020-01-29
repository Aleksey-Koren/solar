/**
 *
 * @param context
 * @param params {{name: '', value: ''}}
 * @constructor
 */
function UserSelector(context, params) {
    var me = this;
    this.filter = {
        title: '',
        login: ''
    };
    this.selector = new GenericSelector({
        context: context,
        popupTitle: "Select User",
        value: params.value,
        name: params.name,
        title: "Select User",
        onRowClick: function(row) {
            me.selector.update(row.title, row.id);
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
                name: "title",
                title: "Title",
            },
            {
                name: "login",
                title: "Login",
            }
        ],
        filters: [
            Dom.el('input', {name: 'userTitle', placeholder: 'User Name', onkeyup: function(e){
                me.filter.title = e.target.value.toLowerCase();
                me.selector.grid.pageInfo.page = 1;
                me.showGrid();
            }}),
            " ",
            Dom.el('input', {name: 'userTitle', placeholder: 'User Login', onkeyup: function(e){
                me.filter.login = e.target.value.toLowerCase();
                me.selector.grid.pageInfo.page = 1;
                me.showGrid();
            }}),
        ]
    });
    this.container = this.selector.container;
}

UserSelector.prototype.unmount = function() {
    this.selector.popup.hide();
};

UserSelector.prototype.showGrid = function() {
    var grid = this.selector.grid;
    grid.data = [];
    grid.render();
    var params = [];
    if(this.filter.login) {
        params.push("login=" + encodeURIComponent(this.filter.login));
    }
    if(this.filter.title) {
        params.push("title=" + encodeURIComponent(this.filter.title));
    }
    Rest.doGet("/api/users?" + params.join("&")).then(function(page){
        grid.setPage(page);
        grid.render();
    });
};

UserSelector.prototype.onValueUpdate = function(value) {
    if(!value) {
        return;
    }
    var me = this;
    Rest.doGet("/api/users/" + value).then(function(user){
        me.selector.update(user.title, user.id);
    });
};
