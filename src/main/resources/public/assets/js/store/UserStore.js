function UserStore() {
    AbstractStore.call(this);
    this.init();
    this.user = null;
    this.name = "user"
}
UserStore.prototype = Object.create(AbstractStore.prototype);

UserStore.prototype.init = function() {
    this.user = null;
    this.isLoaded = false;
};

UserStore.prototype.clean = function() {
    this.init();
    this.notify();
};

UserStore.prototype.setUser = function(user) {
    this.user = user;
    this.isLoaded = true;
    this.notify(this.user)
};
UserStore.prototype.update = function() {

};
UserStore.prototype.processToken = function(token) {
    function parseJwt (token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    }
    var user = parseJwt(token);
    this.setUser(user);
}