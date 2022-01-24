/**
 * @param currentUser {number}
 * @param openInvitationPopup {VoidFunction}
 * @constructor
 */
function ChatTitle(currentUser, openInvitationPopup) {
    this.currentUser = currentUser
    this.openInvitationPopup = openInvitationPopup
    this.room = null;
    this.title = "";
    this.container = Dom.el('div', 'chat-head');
    this.editTitle = null;
    var me = this;
    this.button = Dom.el('a', {
        href: '/#',
        class: 'edit-icon-selected',
        style: {width: '16px', height: '16px', display: 'inline-block'},
        onclick: function(e) {
            e.preventDefault();
            me.editMessage = me.title;
            Dom.clear(me.container);

            me.canvas = Dom.el('textarea', {
                value: me.editMessage,
                onchange: function(e) {
                    me.editTitle = e.target.value;
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
            Dom.append(me.container, me.canvas);
            me.canvas.focus();
        }
    });
    this.setTitle(this.title)
}

ChatTitle.prototype.setTitle = function(title) {
    this.title = title;
    var me = this;
    this.editTitle = null;
    Dom.clear(this.container);
    this.canvas = Dom.el('h3', 'chat-title', [this.title, " ", this.button]);
    Dom.append(this.container, [this.canvas, Dom.el('a', {
        href: '/#',
        style: {width: '16px', height: '16px', display: 'inline-block'},
        class: "icon-add-user",
        onclick: function(e) {
            e.preventDefault();
            me.openInvitationPopup();
        }})]);
}
/**
 *
 * @param room {Room}
 * @returns {string|*}
 */
ChatTitle.prototype.setRoom = function(room) {
    this.room = room;
    var me = this;
    var titleData = ChatTitle.handleRoomTitle(room, me.currentUser);
    this.title = titleData.title;
    this.button.style.display = titleData.editTitle ? "inline-block" : "none";
    this.setTitle(this.title);
}
ChatTitle.prototype.tryEdit = function() {
    var me = this;
    if(me.editTitle === me.title) {
        me.setTitle(me.editTitle);
        return;
    }
    Rest.doPatch("/api/chat/room/" + me.room.id + "/title", me.editTitle).then(function(){
        me.title = me.editTitle;
        me.editTitle = null;
        me.setTitle(me.title);
    }).catch(function(e) {
        console.error(e);
        Notification.error("Fail to update chat title")
    });
}

/**
 *
 * @param room {Room}
 * @param currentUser {number}
 */
ChatTitle.handleRoomTitle = function(room, currentUser) {
    if(room.roomType === 'PUBLIC') {
        return {
            title: room.title,
            editTitle: true
        }
    } else if(room.roomType === 'SYSTEM') {
        return {
            title: "SYSTEM",
            editTitle: false
        }
    } else if(room.roomType === 'PRIVATE') {
        try {
            return {
                title: JSON.parse(room.title).map(function(t) {
                    return t.split(":");
                }).filter(function(t) {
                    return parseInt(t[0]) !== currentUser;
                })[0][1],
                editTitle: false
            }
        } catch (e) {
            console.error(e);
            return {
                title: room.title,
                editTitle: false
            }
        }
    } else {
        return {
            title: 'ROOM-OF-UNKNOWN-TYPE',
            editTitle: false
        }
    }
}