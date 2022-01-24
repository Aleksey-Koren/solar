/**
 * @param context {AppContext}
 * @constructor
 */
function ChatMain(context) {
    this.chatPopup = null;
    this.userStore = context.stores.userStore;
    this.rooms = [];
    this.context = context;
    this.room = null;//id of active room;
    this.chatList = null;//chat list container
    this.chatControls = null;//chat controls container - invite to chat, search for particular chat, etc
    this.chatBody = null;//chat body container (messages from particular chat)
    this.chatBodyControls = null;//controls to post message
    var me = this;
    this.chatTitle = new ChatTitle(this.userStore.user.user_id, function() {
        var room = null;
        for(var i = 0; i < me.rooms.length; i++) {
            if(me.rooms[i].id === me.room) {
                room = me.rooms[i];
                break;
            }
        }
        if(room) {
            me.invitationPopup && me.invitationPopup.unmount();
            me.invitationPopup = new ChatInvitationPopup(context, function(user) {
                return me.resolveInvitation(user, room);
            });
            me.invitationPopup.show(room, me.participants, ChatTitle.handleRoomTitle(room, me.context.stores.userStore.user.user_id).title);
        } else {
            Notification.error("Can't invite users into room which does not exists or is not loaded");
        }
    });
    this.currentMessages = [];
    this.participants = {};
    this.invitationPopup = null;
    var socket = new SockJS('/api/ws');
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({'auth_token': context.loginStorage.getItem('token')}, function() {
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

}

ChatMain.prototype.showChat = function() {
    if(this.chatPopup) {
        this.chatPopup.show();
        this.loadChats();
        return;
    }
    this.createChatList();
    this.createChatBody();
    this.createChatBodyControls();
    this.chatPopup = new Popup({
        context: this.context,
        content: Dom.el('div', 'chat-content', [
            Dom.el('div', 'chat-sidebar', [
                this.chatControls,
                this.chatList
            ]),
            Dom.el('div', 'chat-main', [
                this.chatTitle.container,
                this.chatBody,
                this.chatBodyControls
            ])
        ])
    });
    this.chatPopup.show();
    this.loadChats();
}

ChatMain.prototype.createChatBody = function() {
    this.chatBody = Dom.el('div', 'chat-body');
}

ChatMain.prototype.createChatBodyControls = function() {
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

ChatMain.prototype.loadChats = function() {
    var me = this;
    Rest.doGet("/api/chat/room/user").then(function(value) {
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

ChatMain.prototype.createChatList = function() {
    this.chatList = Dom.el('div', 'chat-list');
    var chatInviteList = Dom.el('div', 'chat-inv-list');
    chatInviteList.style.display = 'none';
    var me = this;

    function hideSearchResutl() {
        Dom.clear(chatInviteList);
        chatInviteList.style.display = 'none';
    }

    var thisUser = me.context.stores.userStore.user.user_id;

    function lookup(value) {
        value = (value || "").trim();
        Dom.clear(chatInviteList);
        if(!value) {
            return;
        }
        var searchResults = {};
        searchResults[thisUser] = true;
        return Rest.doGet("/api/chat/room?participants=2" +
            "&withParticipants=true" +
            "&title=" + encodeURIComponent(value) +
            "&userId=" + thisUser
        ).then(function(existingRooms) {
            var added = false;
            existingRooms.forEach(function(room) {
                var target = room.participants.filter(function(p) {
                    return p.id !== thisUser && p.title.indexOf(value) > -1
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
                Dom.append(chatInviteList, Dom.el('div', null, "No more users with such name"))
            }
            return response;
        }).catch(function(e){
            console.log(e)
            Notification.error("Fail to load users")
        })
    }
    var chatInviteContainer = Dom.el('div', null, [
        Dom.el('input', {class: 'chat-new-chat', placeholder: "Search (3 chars min)", onblur: function(){
                setTimeout(function() {
                    hideSearchResutl();
                }, 5000)
            }, onkeyup(e) {
                if (e.key === 'Enter' || e.keyCode === 13 || e.target.value.length >= 3) {
                    lookup(e.target.value)
                }
            }}),
        chatInviteList
    ]);
    this.chatControls = Dom.el('div', 'chat-controls', [
        chatInviteContainer
    ]);
}


ChatMain.prototype.createRooms = function() {
    var me = this;
    var out = this.rooms.map(function(room) {
        me.subscribeToRoom(room);
        var titleData = ChatTitle.handleRoomTitle(room, me.context.stores.userStore.user.user_id);
        var unread = Dom.el('sup', {class: 'chat-unread', id: 'chat-unread-' + room.id}, room.amount && room.amount !== '0' ? "+" + room.amount : null);
        return Dom.el('div', 'chat-room', Dom.el('a', {href: '/#', click: function(e) {
                e.preventDefault();
                me.openRoom(room);
                Dom.clear(unread);
            }}, [titleData.title, unread]));
    })
    if(out.length === 0) {
        out.push(Dom.el("div", 'chat-room', "no chats"))
    }
    return out;
}

ChatMain.prototype.subscribeToRoom = function(room) {
    var me = this;
    try {
        me.stompClient.subscribe("/room/" + room.id, function(response) {
            var message = JSON.parse(response.body);
            me.appendMessage(message);
            if(room.id === me.room) {
                me.updateLastSeen()
            }
            for(var i = 0; i < me.rooms.length; i++) {
                var subsRoom = me.rooms[i];
                if(subsRoom.id === message.roomId) {
                    if(subsRoom.id !== me.room) {
                        subsRoom.amount++;
                        var unread = document.getElementById('chat-unread-' + subsRoom.id );
                        if(unread) unread.innerHTML = "+" + subsRoom.amount;
                    }
                    break;
                }
            }
        });
    } catch (e) {
        console.error(e);
    }
}

ChatMain.prototype.sendMessage = function(message) {
    this.stompClient.send("/chat/" + this.room, {}, JSON.stringify({
        senderId: this.userStore.user.user_id,
        message:message
    }));
}

/**
 * @param room {Room}
 */
ChatMain.prototype.openRoom = function(room) {
    Dom.clear(this.chatBody);
    this.currentMessages = [];
    var me = this;
    me.room = room.id;
    me.chatTitle.setRoom(room);
    Rest.doGet("/api/chat/room/" + room.id + "/participants").then(function(participants) {
        me.participants = {};
        participants.forEach(function(p) {
            me.participants[p.id] = p;
        })
        return participants;
    }).then(function() {
        return Rest.doGet("/api/chat/room/" + room.id + "/messages");
    }).then(function(page) {
        if(me.room !== room.id) {
            return null;
        }
        var messages = page.content.reverse();
        Dom.clear(me.chatBody);
        messages.map(function(message) {me.appendMessage(message)});
        return page;
    }).then(function(response){
        if(!response) {
            return;
        }
        me.updateLastSeen()
    });
}

ChatMain.prototype.updateLastSeen = function() {
    Rest.doPut("/api/chat/room/" + this.room + "/lastSeenAt");
}
/**
 * @param message {Message}
 */
ChatMain.prototype.appendMessage = function(message) {
    if(message.roomId !== this.room) {
        return;
    }
    var me = this;
    for(var i = 0; i < this.currentMessages.length; i++) {
        if(this.currentMessages[i].message.id === message.id) {
            //@todo
            this.currentMessages[i].update(message);
            return;
        }
    }
    var mes = new ChatMessage(message, this.participants[message.senderId], me.context.stores.userStore.user.user_id, function(newMessage) {
        me.stompClient.send("/chat/" + me.room, {}, JSON.stringify({
            id: message.id,
            senderId: me.context.stores.userStore.user.user_id,
            message: newMessage
        }));
    });
    this.currentMessages.push(mes);
    Dom.append(this.chatBody, mes.container);
    mes.scroll();
}

ChatMain.prototype.unmount = function() {
    if(this.stompClient != null) {
        this.stompClient.disconnect();
    }
    this.userStore.remove(this);
    this.invitationPopup && this.invitationPopup.unmount();
}
/**
 *
 * @param user {Participant}
 * @param room {Room}
 */
ChatMain.prototype.resolveInvitation = function(user, room) {
    var roomPromise;
    var me = this;
    if(room.roomType === 'PUBLIC') {
        roomPromise = new Promise(function(resolve) {
            resolve(room);
        })
    } else if(room.roomType === 'PRIVATE') {
        var oldUser = null;
        for(var id in this.participants) {
            var u = this.participants[id];
            if(u.id === this.context.stores.userStore.user.user_id) {
                continue;
            }
            oldUser = u;
            break;
        }
        if(!oldUser) {
            Notification.error("Fail to invite user into current room, please try to refresh browser")
            return new Promise(function(resolve) {
                resolve(null);
            })
        }
        roomPromise = Rest.doPost("/api/chat/room", {isPrivate: false, userId: oldUser.id}).then(function(room) {
            me.openRoom(room);
            return room;
        });
    } else {
        Notification.error("You can't invite users into current room (reason: room type is " + room.roomType + ").")
        return new Promise(function(resolve) {
            resolve(null);
        })
    }
    return roomPromise.then(function(room) {
        return Rest.doPatch("/api/chat/room/" + room.id + "/participants", user.id)
    }).then(function(response) {
        me.participants[user.id] = user;
        return response;
    }).catch(function(e) {
        console.error(e);
        Notification.error("Fail to add participant into group");
    });
}