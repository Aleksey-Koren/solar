var Notification = {
    data: [],
    container: Dom.el('div', 'notification-container'),
    info: function (params) {
        return Notification._add(params, 'info');
    },
    error: function (params) {
        return Notification._add(params, 'error');
    },
    _add: function (params, type) {
        var message = typeof params === 'string' ? params : params.message;
        var duration = params.duration || 5000;
        var notification = Dom.el('div', {class: 'notification notification-' + type}, Dom.el('div', {}, [
            Dom.el('a', {
                href: '#', class: "notification-close", onclick: function (e) {
                    e.preventDefault();
                    Notification._remove(notification);
                }
            }, 'X'),
            message
        ]));
        if (duration > -1) {
            setTimeout(function () {
                Notification._remove(notification);
            }, duration)
        }
        Dom.append(Notification.container, notification);
    },
    _remove: function (notification) {
        if (notification.parentElement) {
            notification.parentElement.removeChild(notification);
        }
    }
};
(function () {
    var interval = setInterval(function () {
        if (!document.body) {
            return;
        }
        document.body.appendChild(Notification.container);
        clearInterval(interval);
    }, 50)
})();