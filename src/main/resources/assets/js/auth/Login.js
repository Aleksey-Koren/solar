function Login(context, openRegister, changeStorage) {
    var w = wrapTableInput;
    var me = this;
    this.context = context;
    this.errors = Dom.el('div');
    var rememberAttr = {
        type: 'checkbox', onchange: function (e) {
            if (e.target.checked) {
                localStorage.setItem('rememberLogin', '1');
                changeStorage(false);
            } else {
                localStorage.removeItem('rememberLogin');
                changeStorage(true);
            }
        }
    };
    if(localStorage.getItem('rememberLogin')) {
        rememberAttr.checked = true;
    }
    this.container = Dom.el('form', {
        submit: function (e) {
            e.preventDefault();
            me.onSubmit();
        }
    }, Dom.el('table', {}, [
        w("login"),
        w("password", Dom.el('input', {name: 'password', type: 'password'})),
        w(' ', [
            Dom.el('input', {type: 'button', onclick: openRegister, value: 'Create New Account'}),
            ' ',
            Dom.el('input', {type: 'submit', value: 'Login'})
        ]),
        w(' ', Dom.el('label', {}, [Dom.el('input', rememberAttr), "Remember me"])),
        w(' ', this.errors)
    ]));
}

Login.prototype.onSubmit = function () {
    var data = Dom.fromForm(this.container);
    if (!data.login || data.login.length < 3) {
        this.errors.innerHTML = 'Login too short';
        return;
    }
    if (!data.password || data.password.length < 3) {
        this.errors.innerHTML = 'Password too short';
        return;
    }
    this.errors.innerHTML = '';
    var me = this;

    Rest.doPost('/api/login', data).then(function (response) {
        if (response.data) {
            Ajax.addStaticHeader('auth_token', response.data);
            me.context.loginStorage.setItem('token', response.data);
            me.context.menu.runApp('dashboard', function(){
                return new DashboardManagement(me.context);
            });
        } else {
            me.errors.innerHTML = "Invalid credentials";
        }
    }).catch(function () {
        me.errors.innerHTML = "Could not login, server error";
    })

};