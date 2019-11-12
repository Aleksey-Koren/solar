function PlanetsManagement(context) {
    this.form = new PlanetsCreationForm(context);
    this.container = Dom.el('div', {}, [this.form.container])
}
PlanetsManagement.prototype.render = function() {
    this.form.mount();
};
PlanetsManagement.prototype.unmount = function() {
    this.form.unmount();
};