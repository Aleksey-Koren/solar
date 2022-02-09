function Register(context, openLogin, postAuth) {
    var w = wrapTableInput;
    var me = this;
    this.context = context;
    this.postAuth = postAuth;
    this.errors = Dom.el('div');
    this.container = Dom.el('form', {submit: function(e){
        e.preventDefault();
        me.onSubmit();
        }}, Dom.el('table', {}, [
        w("Login Or Email", Dom.el('div', {}, [
            Dom.el('input', {name: 'login'}),
            Dom.el('br'),
            "Login should be 3 or more characters in length",
            Dom.el('br'),
            "You may use email address or any other unique name. We have no email verification.",
            Dom.el('br'),
            "However, restore access to account only possible if we know your email"
        ])),
        w("password", Dom.el('div', {}, [
            Dom.el('input', {type: 'password', name: 'password'}),
            Dom.el('br'),
            "Password should be 3 or more characters in length"
        ])),
        w(' ', [
            Dom.el('input', {type: 'button', onclick: openLogin, value: 'Back to Login'}),
            ' ',
            Dom.el('input', {type: 'submit', value: 'Register'})
        ]),
        w(' ', this.errors)
    ]));
}

Register.prototype.onSubmit = function() {
    var data = Dom.fromForm(this.container);
    if(!data.login || data.login.length < 3) {
        this.errors.innerHTML = 'Login too short';
        return;
    }
    if(!data.password || data.password.length < 3) {
        this.errors.innerHTML = 'Password too short';
        return;
    }
    this.errors.innerHTML = '';
    var me = this;

    Rest.doPost('/api/register', data).then(function (response) {
        if(response.success) {
            me.postAuth(response.token);
        } else {
            me.errors.innerHTML = response.error;
        }
    }).catch(function(){
        me.errors.innerHTML = "Could not register user, server error";
    })

};