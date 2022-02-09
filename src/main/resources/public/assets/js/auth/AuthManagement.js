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
        this.showLogin();
    }
}

AuthManagement.prototype.showLogin = function() {
    Dom.append(this.content, this.app.container);
    Dom.removeClass(this.container, 'hidden');
    this.context.stores.userStore.clean();
};
AuthManagement.prototype.createLogin = function() {
    var me = this;
    return new Login(this.context, function(){
        me.app = me.createRegister();
        me.content.innerHTML = '';
        me.content.appendChild(me.app.container);
    }, function(isSession){
        me.context.loginStorage = isSession ? sessionStorage : localStorage;
    }, function(token) {
        me.postAuth(token);
    })
};

AuthManagement.prototype.createRegister = function() {
    var me = this;
    return new Register(this.context, function(){
        me.app = me.createLogin();
        me.content.innerHTML = '';
        me.content.appendChild(me.app.container);
    }, function(token) {
        me.postAuth(token);
    })
};

AuthManagement.prototype.authorise = function(token) {
    var me = this;
    Rest.doGet('/api/refresh', {'auth_token': token}).then(function(response){
        if(response.data) {
            me.postAuth(response.data);
        } else {
            me.showLogin();
        }
    }).catch(function(e){
        console.error("could not authorise", e);
        me.showLogin();
    })
};

AuthManagement.prototype.postAuth = function(token) {
    var me = this;
    Ajax.addStaticHeader('auth_token', token);
    me.context.loginStorage.setItem('token', token);
    me.context.stores.userStore.processToken(token);
    me.context.menu.runApp('solarWrapper', function(){
        return new SolarWrapper(me.context);
    });
}

AuthManagement.prototype.render = function() {

};