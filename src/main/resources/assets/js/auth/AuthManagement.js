function AuthManagement(context) {
    var me = this;
    this.context = context;
    this.app = this.createLogin();
    this.content = Dom.el('div');
    this.container = Dom.el('div', {class: "overlay hidden"}, Dom.el('div', {class: "small-modal login-modal"}, this.content));

    var token = this.context.loginStorage.getItem('token');
    if(token) {
        this.authorise(token);
    } else {
        Dom.append(this.content, this.app.container);
        Dom.removeClass(this.container, 'hidden')
    }
}

AuthManagement.prototype.createLogin = function() {
    var me = this;
    return new Login(this.context, function(){
        me.app = me.createRegister();
        me.content.innerHTML = '';
        me.content.appendChild(me.app.container);
    }, function(isSession){
        me.context.loginStorage = isSession ? sessionStorage : localStorage;
    })
};

AuthManagement.prototype.createRegister = function() {
    var me = this;
    return new Register(this.context, function(){
        me.app = me.createLogin();
        me.content.innerHTML = '';
        me.content.appendChild(me.app.container);
    })
};

AuthManagement.prototype.authorise = function(token) {
    var me = this;
    Rest.doPost('/api/authorise', {data: token}).then(function(response){
        if(response.data) {
            me.context.loginStorage.setItem('token', response.data);
            Ajax.addStaticHeader('auth_token', response.data);
            me.context.menu.runApp('dashboard', function(){
                return new DashboardManagement(me.context);
            });
        } else {
            Dom.removeClass(me.container, 'hidden')
        }
    }).catch(function(){
        console.error("could not authorise");
        Dom.append(me.content, me.app.container);
    })
};

AuthManagement.prototype.render = function() {

};