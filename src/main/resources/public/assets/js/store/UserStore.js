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