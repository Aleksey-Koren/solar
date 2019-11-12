function UserManagement(context) {
    this.context = context;
    this.container = Dom.el('div');
}

UserManagement.prototype.render = function() {
    this.openGrid();
};

UserManagement.prototype.openForm = function(user) {
    this.container.innerHTML = '';
    var me = this;
    this.container.appendChild(new UserForm(this.context, user, function(){
       me.openGrid();
    }).container)
};
UserManagement.prototype.openGrid = function() {
    this.container.innerHTML = '';
    var me = this;
    this.container.appendChild(new UserGrid(function(user){
       me.openForm(user);
    }).container)
};