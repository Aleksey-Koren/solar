function Login(context, openRegister, changeStorage, postAuth) {
    var w = wrapTableInput;
    var me = this;
    me.postAuth = postAuth;
    this.context = context;
    this.errors = Dom.el('div');
    this.blockedInterval = null;
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

Login.prototype.toTimer = function (time) {
    var seconds = parseInt(time / 1000);
    var minutes = parseInt(seconds / 60);
    var renderSeconds = (seconds - minutes * 60);
    if(renderSeconds < 10) {
        renderSeconds = "0" + renderSeconds;
    }
    return minutes + ":" + renderSeconds;
};

Login.prototype.onSubmit = function () {
    if(this.blockedInterval) {
        clearInterval(this.blockedInterval);
    }
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
            me.postAuth(response.data);
        } else {
            if(response.blocked) {
                me.blockedInterval = setInterval(function(){
                    me.errors.innerHTML = 'Account will be unlocked after ' + me.toTimer(response.blocked -= 1000);
                    if(response.blocked <= 0) {
                        clearInterval(me.blockedInterval);
                        me.errors.innerHTML = '';
                    }
                }, 1000);

            } else {
                me.errors.innerHTML = "Invalid credentials";
            }
        }
    }).catch(function () {
        me.errors.innerHTML = "Could not login, server error";
    })

};