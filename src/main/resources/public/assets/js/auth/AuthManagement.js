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
    Rest.doGet('/api/refresh', {'auth_token': token}).then(function(response){
        if(response.data) {
            me.context.loginStorage.setItem('token', response.data);
            Ajax.addStaticHeader('auth_token', response.data);
            me.context.menu.runApp('dashboard', function(){
                return new DashboardManagement(me.context);
            });

            function parseJwt (token) {
                var base64Url = token.split('.')[1];
                var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));

                return JSON.parse(jsonPayload);
            }
            var user = parseJwt(response.data);
            me.context.stores.userStore.setUser(user);
        } else {
            me.showLogin();
        }
    }).catch(function(e){
        console.error("could not authorise", e);
        me.showLogin();
    })
};

AuthManagement.prototype.render = function() {

};