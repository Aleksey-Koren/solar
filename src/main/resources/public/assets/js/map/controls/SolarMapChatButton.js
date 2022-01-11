/**
 * @param solarMap {SolarMap}
 * @constructor
 */
function SolarMapChatButton( solarMap) {
    var me = this;
    this.chatPopup = null;
    this.solarMap = solarMap;
    this.rooms = [];
    this.room = null;//id of active room;
    this.chatList = null;//chat list container
    this.chatControls = null;//chat controls container - invite to chat, search for particular chat, etc
    this.chatBody = null;//chat body container (messages from particular chat)
    this.chatBodyControls = null;//controls to post message
    this.container = Dom.el('div', {class: 'solar-map-control', onclick: function(){
            me.showChat();
        }
    }, [
        new SolarMapIcon("solid", 'comments').icon
    ]);
}

SolarMapChatButton.prototype.createChatList = function() {
    this.chatList = Dom.el('div', 'chat-list');
    var chatInviteList = Dom.el('div');
    var me = this;
    function lookup(value) {
        value = (value || "").trim();
        Dom.clear(chatInviteList);
        if(!value) {
            return;
        }
        var searchResults = {};
        searchResults[me.solarMap.context.stores.userStore.user.user_id] = true;
        return Rest.doGet("/api/chat/room?participants=2" +
            "&withParticipants=true" +
            "&title=" + encodeURIComponent(value) +
            "&userId=" + me.solarMap.context.stores.userStore.user.user_id
        ).then(function(existingRooms) {
            existingRooms.forEach(function(room) {
                var target = room.participants.filter(function(p) {
                    return p.id !== me.solarMap.context.stores.userStore.user.user_id && p.title.indexOf(value) > -1
                })
                if(target.length !== 1) {
                    return;
                }
                searchResults[target[0].id] = true;
                Dom.append(chatInviteList, Dom.el('div', 'chat-search-result',
                    Dom.el('a', {href: '/#', onclick: function(e){
                        e.preventDefault();
                        me.openRoom(room);
                    }}, target[0].title)));
            })
            return existingRooms;
        }).then(function() {
            return Rest.doGet("/api/users?page=0&size=50&title=" + encodeURIComponent(value))}
        ).then(function(response) {
            var users = response.content
            Dom.append(chatInviteList, users.filter(function(user){
                return !searchResults[user.id];
            }).map(function(user){
                return Dom.el('div', null, Dom.el('a', {href: '/#', onclick: function(e){
                        e.preventDefault();
                        Rest.doPost("/api/chat/room", {userId: user.id, isPrivate: true}).then(room => {
                            me.rooms.push(room);
                            me.openRoom(room);
                        })
                    }}, user.title || user.id));
            }))
            return response;
        }).catch(function(e){
            console.log(e)
            Notification.error("Fail to load users")
        })
    }
    var chatInviteContainer = Dom.el('div', null, [
        Dom.el('input', {class: 'chat-new-chat', placeholder: "Search", onblur: function(e){
                lookup(e.target.value);
                setTimeout(function() {
                    Dom.clear(chatInviteList);
                }, 5000)
            }, onkeyup(e) {
                if (e.key === 'Enter' || e.keyCode === 13) {
                    lookup(e.target.value)
                }
            }}),
        chatInviteList
    ]);
    this.chatControls = Dom.el('div', 'chat-controls', [
        chatInviteContainer
    ]);
}
SolarMapChatButton.prototype.createChatBody = function() {
    this.chatBody = Dom.el('div', 'chat-body');
}
SolarMapChatButton.prototype.showChat = function() {
    if(this.chatPopup) {
        this.chatPopup.show();
        this.loadChats();
        return;
    }
    this.createChatList();
    this.createChatBody();
    this.chatPopup = new Popup({
        context: this.solarMap.context,
        content: [
            Dom.el('div', 'chat-sidebar', [
                this.chatControls,
                this.chatList
            ]),
            Dom.el('div', 'chat-main', [
                this.chatBody,
                this.chatBodyControls
            ])
        ],
        title: "Chat"
    });
    this.chatPopup.show();
    this.loadChats();
}

SolarMapChatButton.prototype.loadChats = function() {
    var me = this;
    Rest.doGet("/api/chat/user/room").then(function(value) {
        me.rooms = value;
        Dom.clear(me.chatList);
        Dom.append(me.chatList, me.createRooms())
    }).catch(function(){
        Notification.error("Fail to load chats")
    })
}

SolarMapChatButton.prototype.createRooms = function() {
    var me = this;
    var out = this.rooms.map(function(room) {
        return Dom.el('div', 'chat-room', Dom.el('a', {href: '/#', click: function(e) {
            e.preventDefault();
            me.openRoom(room);
        }}), room.title);
    })
    if(out.length === 0) {
        out.push(Dom.el("div", 'chat-room', "no chats"))
    }
    return out;
}

SolarMapChatButton.prototype.openRoom = function(room) {
    Dom.clear(this.chatBody);
    var me = this;
    me.room = room.id;
    Rest.doGet("/api/chat/room/" + room.id + "/messages").then(function(messages) {
        if(me.room !== room.id) {
            return;
        }
        Dom.clear(me.chatBody);
        Dom.append(me.chatBody, messages.map(function(message) {
            Dom.el('div', 'chat-message', JSON.stringify(message))
        }))
    });
}
