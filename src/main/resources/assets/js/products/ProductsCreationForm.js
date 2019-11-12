function ProductsCreationForm(id, onClose) {
    var w = wrapTableInput;

    var me = this;
    this.id = id;
    this.onClose = onClose;
    this.title = Dom.el('h3', {}, !id ? "Create new Product Type" : "");
    this.form = Dom.el('form', {
        class: "products-form", onsubmit: function (e) {
            me.onSubmit(e)
        }
    }, [
        Dom.el('input', {name: 'id', type: 'hidden'}),
        Dom.el('table', {}, [
            w('title'),
            w('bulk'),
            w('mass'),
            w('price'),
            w(' ', Dom.el('div', {}, [
                Dom.el('input', {type: 'button', onclick: function() {me.onClose()}, value: "Back"}),
                ' ',
                Dom.el('input', {type: 'submit', value: "Save"})
            ]))
        ])
    ]);

    this.container = Dom.el('div', {});

    if(id) {
        this.load(id);
    }

    Dom.append(this.container, [
        this.title,
        this.form
    ]);
}

ProductsCreationForm.prototype.load = function () {
    var me = this;
    Rest.doGet('/api/product/' + this.id).then(function (value) {
        Dom.form(me.form, value);
    });
};

ProductsCreationForm.prototype.onSubmit = function (e) {
    if (e) e.preventDefault();
    var data = Dom.fromForm(this.form);
    var me = this;
    Rest.doPost('/api/product', data).then(function (response) {
        me.onClose();
        Notification.info("Product was created (updated)");
    }).catch(function() {
        Notification.error("Can't create (update) product");
    });
};