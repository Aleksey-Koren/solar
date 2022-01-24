/**
 * @param context {AppContext}
 * @param addParticipant {PromiseSupplier}
 * @constructor
 */
function ChatInvitationPopup(context, addParticipant) {

    this.invitationPopup = new Popup({
        context: context,
        content: Dom.el('div')
    });
    this.addParticipant = addParticipant;
    this.context = context;
    this.room = null;//chat active room;
    this.participants = {};
    this.chatInvPanel = Dom.el('div');
    this.chatParticipantsPanel = Dom.el('div', 'chat-inv-panel', Dom.el('h4', null,'Chat participants:'));

}

ChatInvitationPopup.prototype.unmount = function() {
    this.invitationPopup.hide();
}
/**
 *
 * @param room {Room}
 * @param participants {{[key:string]:Participant}}
 * @param title {string}
 */
ChatInvitationPopup.prototype.show = function(room, participants, title) {
    var me = this;
    me.participants = participants;
    this.invitationPopup.setContent(Dom.el('div', 'chat-inv-main', [
        Dom.el('div', 'chat-inv-search', [
            Dom.el('h3', 'chat-inv-title', title),
            Dom.el('div', 'chat-search-panel', [
                Dom.el('input', {
                    placeholder: "Search (3 chars min)",
                    onkeyup: function(e) {
                        var value = e.target.value;
                        if (e.key === 'Enter' || e.keyCode === 13 || value.length >= 3) {
                            me.lookup(value)
                        }
                    }
                }),
                this.chatInvPanel
            ]),
        ]),
        this.chatParticipantsPanel
    ]));
    for(var id in participants) {
        this.renderParticipant(participants[id])
    }
    this.invitationPopup.show();
}

/**
 *
 * @param participant {Participant}
 */
ChatInvitationPopup.prototype.renderParticipant = function(participant) {
    Dom.append(this.chatParticipantsPanel, Dom.el('div', null,"#" + participant.id + ":" + participant.title));
}

ChatInvitationPopup.prototype.lookup = function(value) {
    var me = this;
    Dom.clear(me.chatInvPanel);
    return Rest.doGet("/api/users?page=0&size=50&title=" + encodeURIComponent(value)).then(function(users) {
        var found = false
        users.content.filter(function(u) {
            return !me.participants[u.id];
        }).forEach(function(u) {
            if(!found) {
                Dom.append(me.chatInvPanel, Dom.el('h4', null, 'Search results: '))
                found = true;
            }
            var el = Dom.el(
                'div',
                null,
                Dom.el('a', {href: '/#', onclick: function(e) {
                        e.preventDefault();
                        me.addParticipant(u).then(function() {
                            el.parentElement.removeChild(el);
                            me.renderParticipant(u);
                            Dom.clear(me.chatInvPanel);
                        });
                    }}, u.title)
            );
            Dom.append(me.chatInvPanel, el);
        })
        if(!found) {
            Dom.append(me.chatInvPanel, Dom.el('h4', null, 'No users with such search criteria'));
        }
    }).catch(function(e) {
        console.error(e);
        Notification.error("Fail to load user list")
    })
}