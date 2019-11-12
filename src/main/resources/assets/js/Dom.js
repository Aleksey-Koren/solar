var Dom = {};

if(typeof Element !== undefined) {
    Element.prototype.isDomElement = true;
}
if(typeof HTMLElement !== undefined) {
    HTMLElement.prototype.isDomElement = true;
}

/**
 * @param type string
 * @param attr object|null
 * @param content string|Element|Element[]
 * @returns {Element}
 */
Dom.el = function (type, attr, content) {
    var o = document.createElement(type);
    if(type === 'input') {
        attr = attr || {};
        if(!attr.autocomplete) {
            attr.autocomplete = 'off';
        }
    }
    Dom.update(o, attr);
    Dom.append(o, content);
    return o;
};
Dom.addClass = function (el, clazz) {
    if (el.className) {
        if (el.className.indexOf(clazz) === -1) {
            el.className += ' ' + clazz;
        } else if (el.className.split(' ').indexOf(clazz) === -1) {
            el.className += ' ' + clazz;
        }
    } else {
        el.className = clazz;
    }
};
Dom.removeClass = function (el, clazz) {
    var cl = el.className;
    if (cl && cl.indexOf(clazz) > -1) {
        var p = cl.split(' ');
        var i = p.indexOf(clazz);
        if (i > -1) {
            p.splice(i, 1);
            el.className = p.join(' ');
        }
    }
};
Dom.hasClass = function (el, clazz) {
    var cl = el.className;
    if (cl.indexOf(clazz) > -1) {
        return cl.split(' ').indexOf(clazz) > -1;
    } else {
        return false;
    }
};
Dom.id = function (id) {
    return document.getElementById(id);
};
Dom.update = function (el, attr) {
    if (typeof attr === 'string') {
        el.className = attr;
    } else if (attr)for (var i in attr) {
        if (!attr.hasOwnProperty(i))continue;
        var value = attr[i];
        if (typeof attr[i] == 'function') {
            var key = i;
            if (key.indexOf("on") === 0) {
                key = key.substring(2);
            }
            el.addEventListener(key, value);
        } else {
            if(i === 'value') {
                el.value = value;
            } else {
                el.setAttribute(i, value)
            }
        }
    }
};
Dom.append = function (o, content) {
    if (!(content === undefined || content === null)) {
        if (typeof content === 'string' || typeof content === 'number') {
            o.appendChild(document.createTextNode(content + ""));
        } else if (content.length !== undefined && content.push && content.pop) {
            for (var i = 0; i < content.length; i++) {
                var child = content[i];
                if (child) {
                    Dom.append(o, child);
                }
            }
        } else {
            //used prototyped property
            if(content.isDomElement) {
                if(!o) {
                    throw new Error("no parent element");
                }
                o.appendChild(content)
            } else if(content.container) {
                Dom.append(o, content.container);
            } else {
                throw new Error("Can't append object")
            }
        }
    }
};
function iterateListeners(el, listeners, clb) {
    for(var key in listeners) {
        if(!listeners.hasOwnProperty(key))continue;
        var wrapper = listeners[key];
        var listnerName = key.indexOf('on') === 0 ? key.substring(2) : key;
        if(typeof wrapper === "function") {
            clb(el, listnerName, wrapper)
        } else {
            for(var i = 0; i < wrapper.length; i++) {
                clb(el, listnerName, wrapper[i]);
            }
        }
    }
}
Dom.addListeners = function(el, listeners) {
    if(!listeners) {
        listeners = el;
        el = window;
    }
    iterateListeners(el, listeners, function(el, key, listener){
        el.addEventListener(key, listener, false);
    })
};
/**
 * Remove event listners
 * @param el Node|object
 * @param listeners object|null
 */
Dom.removeListeners = function(el, listeners) {
    if(!listeners) {
        listeners = el;
        el = window;
    }
    iterateListeners(el, listeners, function(el, key, listener){
        el.removeEventListener(key, listener, false);
    })
};

Dom.calculateOffset = function (elem) {
    var top = 0, left = 0;
    if (elem.getBoundingClientRect) {
        var box = elem.getBoundingClientRect();

        var body = document.body;
        var docElem = document.documentElement;

        var scrollTop = window.pageYOffset || docElem.scrollTop || body.scrollTop;
        var scrollLeft = window.pageXOffset || docElem.scrollLeft || body.scrollLeft;

        var clientTop = docElem.clientTop || body.clientTop || 0;
        var clientLeft = docElem.clientLeft || body.clientLeft || 0;

        top = box.top + scrollTop - clientTop;
        left = box.left + scrollLeft - clientLeft;

        return {top: Math.round(top), left: Math.round(left)}
    } else {
        while (elem) {
            top = top + parseInt(elem.offsetTop);
            left = left + parseInt(elem.offsetLeft);
            elem = elem.offsetParent
        }
        return {top: top, left: left}
    }
};
Dom.insert = function(el, content, before) {
    if(el.innerHTML === '') {
        Dom.append(el, content);
        return;
    }
    if(!before)before = el.childNodes[0];

    if (content) {
        if (typeof content === 'string' || typeof content === 'number') {
            el.insertBefore(document.createTextNode(content + ""), before);
        } else if (content.length && content.push && content.pop) {
            for (var i = 0; i < content.length; i++) {
                var child = content[i];
                if (child) {
                    Dom.insert(el, child, before);
                }
            }
        } else {
            //used prototyped property
            if(content.isDomElement) {
                el.insertBefore(content, before);
            } else if(content.container) {
                Dom.insert(el, content.container, before);
            } else {
                throw "Can't inesert object"
            }
        }
    }
};

Dom.form = function(form, object) {
    var elements = form.querySelectorAll('input, textarea, select');
    var map = {};
    for(var i = 0; i < elements.length; i++) {
        var name = elements[i].name;
        if(!name)continue;
        if(map[name])throw new Error("Two controls with same same");
        map[name] = elements[i];
        elements[i].value = '';
    }
    for(var k in object) {
        if(object.hasOwnProperty(k)) {
            if(map[k]) {
                map[k].value = object[k] || object[k] === 0 ? object[k] + '' : '';
            }
        }
    }
};

Dom.fromForm = function (form, obj) {
    var elements = form.querySelectorAll('input, textarea, select');
    var out = obj || {};
    for(var i = 0; i < elements.length; i++) {
        var el = elements[i];
        if(el.name) {
            out[el.name] = el.value;
        }
    }
    return out;
};
Dom.clearForm = function (form) {
    var elements = form.querySelectorAll('input, textarea, select');
    for(var i = 0; i < elements.length; i++) {
        var el = elements[i];
        if(el.name) {
            el.value = '';
        }
    }
};