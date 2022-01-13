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
    this.chatTitle = null;//header in chat

    var socket = new SockJS('/api/ws');
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({'auth_token': solarMap.context.loginStorage.getItem('token')}, function() {
        me.stompClient.subscribe('/user/notifications', function(response) {
            var object = JSON.parse(response.body);
            switch (object.type) {
                case "INVITED_TO_ROOM":
                    me.rooms.push(object.payload);
                    Dom.clear(me.chatList);
                    Dom.append(me.chatList, me.createRooms())
                    break;
                default:
                    console.error("unknown object type in user subscription: " + response.body);
            }
        });
    }, function(response) {
        Notification.error("Fail connect to chat socket");
        console.error(response);
    });

    this.container = Dom.el('div', {class: 'solar-map-control', onclick: function(){
            me.showChat();
        }
    }, [
        new SolarMapIcon("solid", 'comments').icon
    ]);
}

SolarMapChatButton.prototype.createChatList = function() {
    this.chatList = Dom.el('div', 'chat-list');
    var chatInviteList = Dom.el('div', 'chat-inv-list');
    chatInviteList.style.display = 'none';
    var me = this;

    function hideSearchResutl() {
        Dom.clear(chatInviteList);
        chatInviteList.style.display = 'none';
    }

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
            var added = false;
            existingRooms.forEach(function(room) {
                var target = room.participants.filter(function(p) {
                    return p.id !== me.solarMap.context.stores.userStore.user.user_id && p.title.indexOf(value) > -1
                })
                if(target.length !== 1) {
                    return;
                }
                if(!added) {
                    added = true;
                    Dom.append(chatInviteList, Dom.el('div', null, 'Existing chats:'))
                    chatInviteList.style.display = 'block';
                }
                searchResults[target[0].id] = true;
                Dom.append(chatInviteList, Dom.el('div', 'chat-search-result',
                    Dom.el('a', {href: '/#', onclick: function(e){
                        e.preventDefault();
                        hideSearchResutl();
                        me.openRoom(room);
                    }}, target[0].title)));
            })
            return existingRooms;
        }).then(function() {
            return Rest.doGet("/api/users?page=0&size=50&title=" + encodeURIComponent(value))}
        ).then(function(response) {
            var users = response.content.filter(function(user){
                return !searchResults[user.id];
            }).map(function(user){
                return Dom.el('div', null, Dom.el('a', {href: '/#', onclick: function(e){
                        e.preventDefault();
                        Rest.doPost("/api/chat/room", {userId: user.id, isPrivate: true}).then(room => {
                            me.rooms.push(room);
                            hideSearchResutl();
                            me.openRoom(room);
                        })
                    }}, user.title || user.id));
            })

            if(users.length) {
                Dom.append(chatInviteList, Dom.el("div", null, 'Start new chat with:'));
                chatInviteList.style.display = 'block';
                Dom.append(chatInviteList, users)
            } else {
                Dom.append(chatInviteList, Dom.el('div', null, "No users with such search criteria"))
            }
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
                    hideSearchResutl();
                }, 50000)
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
    this.chatTitle = Dom.el('h3', 'chat-title')
}
SolarMapChatButton.prototype.showChat = function() {
    if(this.chatPopup) {
        this.chatPopup.show();
        this.loadChats();
        return;
    }
    this.createChatList();
    this.createChatBody();
    this.createChatBodyControls();
    this.chatPopup = new Popup({
        context: this.solarMap.context,
        content: Dom.el('div', 'chat-content', [
            Dom.el('div', 'chat-sidebar', [
                this.chatControls,
                this.chatList
            ]),
            Dom.el('div', 'chat-main', [
                this.chatTitle,
                this.chatBody,
                this.chatBodyControls
            ])
        ])
    });
    this.chatPopup.show();
    this.loadChats();
}

SolarMapChatButton.prototype.createChatBodyControls = function() {
    var message = Dom.el('textarea');
    var me = this;
    var button = Dom.el('input', {type: 'button', value: "Send", onclick: function(){
        var value = message.value.trim();
        if(value) {
            me.sendMessage(value);
            message.value = '';
        }
    }})
    this.chatBodyControls = Dom.el('div', 'chat-body-controls', [
        message, button
    ])
}
SolarMapChatButton.prototype.loadChats = function() {
    var me = this;
    Rest.doGet("/api/chat/user/room").then(function(value) {
        me.rooms = value;
        Dom.clear(me.chatList);
        Dom.append(me.chatList, me.createRooms())
        if(me.rooms.length > 0) {
            me.openRoom(me.rooms[0]);
        }
    }).catch(function(){
        Notification.error("Fail to load chats")
    })
}

SolarMapChatButton.prototype.createRooms = function() {
    var me = this;
    var out = this.rooms.map(function(room) {
        me.subscribeToRoom(room);
        var unread = Dom.el('sup', {class: 'chat-unread', id: 'chat-unread-' + room.id}, room.amount && room.amount !== '0' ? "+" + room.amount : null);
        return Dom.el('div', 'chat-room', Dom.el('a', {href: '/#', click: function(e) {
            e.preventDefault();
            me.openRoom(room);
            Dom.clear(unread);
        }}, [room.title || room.id, unread]));
    })
    if(out.length === 0) {
        out.push(Dom.el("div", 'chat-room', "no chats"))
    }
    return out;
}

SolarMapChatButton.prototype.subscribeToRoom = function(room) {
    var me = this;
    try {
        me.stompClient.subscribe("/room/" + room.id, function(response) {
            var message = JSON.parse(response.body);
            me.appendMessage(message);
            for(var i = 0; i < me.rooms.length; i++) {
                var room = me.rooms[i];
                if(room.id === message.roomId) {
                    if(room.id !== me.room) {
                        room.amount++;
                        var unread = document.getElementById('chat-unread-' + room.id );
                        if(unread) unread.innerHTML = "+" + room.amount;
                    }
                    break;
                }
            }
        });
    } catch (e) {
        console.error(e);
    }


}
SolarMapChatButton.prototype.sendMessage = function(message) {
    this.stompClient.send("/chat/" + this.room, {}, JSON.stringify({
        senderId: this.solarMap.context.stores.userStore.user.user_id,
        message:message
    }));
}

SolarMapChatButton.prototype.openRoom = function(room) {
    Dom.clear(this.chatBody);
    var me = this;
    me.room = room.id;
    me.chatTitle.innerHTML = room.title;
    Rest.doGet("/api/chat/room/" + room.id + "/messages").then(function(page) {
        if(me.room !== room.id) {
            return;
        }
        var messages = page.content.reverse();
        Dom.clear(me.chatBody);
        messages.map(function(message) {me.appendMessage(message)});
    });
}



SolarMapChatButton.prototype.appendMessage = function(message) {
    if(message.roomId !== this.room) {
        return;
    }
    var mes = Dom.el('div', 'chat-message', [
        Dom.el('div', null, message.senderId + " " + message.createdAt),
        Dom.el('div', null, message.message),
    ]);
    Dom.append(this.chatBody, mes)
    mes.scrollIntoView({behavior: 'smooth'});
}
SolarMapChatButton.prototype.unmount = function() {
    if(this.stompClient != null) {
        this.stompClient.disconnect();
    }
}