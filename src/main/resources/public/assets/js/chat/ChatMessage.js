/**
 * @param message {Message}
 * @param sender {{id:number, title: string}}
 * @param currentUser {number}
 * @param sendUpdate {PromiseSupplier}
 * @constructor
 */
function ChatMessage(message, sender, currentUser, sendUpdate) {
    this.canvas = Dom.el('div', null, message.message);
    this.canvasContainer = Dom.el('div', null, this.canvas);
    this.message = message;
    this.sendUpdate = sendUpdate;
    var me = this;
    this.editMessage = null;
    this.container = Dom.el('div', 'chat-message ' + (currentUser === message.senderId ? "chat-your-message" : ""), [
        Dom.el('div', null, [
            (currentUser === message.senderId ? "" : (sender||{}).title + " ")
            + this.renderDate(message.createdAt)
            + " ",
            currentUser === message.senderId ? Dom.el('a', {
                href: '/#',
                class: 'edit-icon',
                style: {width: '16px', height: '16px', display: 'inline-block'},
                onclick: function(e) {
                    e.preventDefault();
                    me.editMessage = me.message.message;
                    me.canvasContainer.removeChild(me.canvas)

                    me.canvas = Dom.el('textarea', {
                        value: message.message,
                        onchange: function(e) {
                            me.editMessage = e.target.value;
                        },
                        onkeyup: function(e) {
                            if ((e.key === 'Enter' || e.keyCode === 13) && e.ctrlKey) {
                                me.tryEdit()
                            }
                        },
                        onblur: function() {
                            me.tryEdit()
                        }
                    });
                    Dom.append(me.canvasContainer, me.canvas);
                    me.canvas.focus();
                }
            }) : null
        ]),
        this.canvasContainer,
    ])
}

ChatMessage.prototype.scroll = function() {
    this.container.scrollIntoView({behavior: 'smooth'});
}
ChatMessage.prototype.tryEdit = function() {
    var me = this;
    if(me.editMessage === me.message.message) {
        me.canvasContainer.removeChild(me.canvas);
        me.canvas = Dom.el('div', null, me.message.message);
        me.editMessage = null;
        Dom.append(me.canvasContainer, me.canvas);
        return;
    }
    this.sendUpdate(me.editMessage)

    me.message.message = me.editMessage;
    me.editMessage = null;
    me.canvasContainer.removeChild(me.canvas);
    me.canvas = Dom.el('div', null, me.message.message);
    Dom.append(me.canvasContainer, me.canvas);

}
ChatMessage.prototype.renderDate = function(str) {
    if(!str) {
        return '';
    }
    var date = new Date(str);
    if(isNaN(date.getDate())) {
        return '';
    }
    return humanDate(date)
}
ChatMessage.prototype.update = function(message) {
    if(this.editMessage === null) {
        this.message.message = message.message;
        this.canvas.innerHTML = message.message;
    }
}