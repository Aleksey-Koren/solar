function LogoutManagement(context){
    localStorage.removeItem('token');
    sessionStorage.removeItem('token');
    this.container = Dom.el('div');
    this.context = context;
}

LogoutManagement.prototype.render = function() {
    var me = this;
    this.context.menu.runApp("authManagement", function(){
        return new AuthManagement(me.context);
    });
};