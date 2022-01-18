/**
 * @param currentUser {number}
 * @constructor
 */
function ChatTitle(currentUser) {
    this.currentUser = currentUser
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
    this.editTitle = null;
    Dom.clear(this.container);
    this.canvas = Dom.el('h3', 'chat-title', [this.title, " ", this.button]);
    Dom.append(this.container, [this.canvas, Dom.el('a', {
        href: '/#',
        style: {width: '16px', height: '16px', display: 'inline-block'},
        class: "icon-add-user",
        onclick: function(e) {
            e.preventDefault();
            this.inviteUser();
        }})]);
}
/**
 *
 * @param room {{id: number, title: string, roomType: 'PRIVATE'|'SYSTEM'|'PUBLIC'}}
 * @returns {string|*}
 */
ChatTitle.prototype.setRoom = function(room) {
    this.room = room;
    var me = this;
    if(this.room.roomType === 'PUBLIC') {
        this.button.style.display = "inline-block";
        this.title = this.room.title;
    } else if(this.room.roomType === 'SYSTEM') {
        this.button.style.display = "none";
        this.title = "SYSTEM";
    } else if(this.room.roomType === 'PRIVATE') {
        this.button.style.display = "none";
        try {
            var titles = JSON.parse(this.room.title);
            this.title = titles.map(function(t) {
                return t.split(":");
            }).filter(function(t) {
                return parseInt(t[0]) !== me.currentUser;
            })[0][1];
        } catch (e) {
            console.error(e);
            this.title = this.room.title;
        }
    } else {
        this.title = 'ROOM-OF-UNKNOWN-TYPE'
        this.button.style.display = "none";
    }
    this.title = this.title || "ROOM-WITHOUT-TITLE"
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