/**
 *
 * @param date {Date}
 * @return {string}
 */
function humanDate(date) {
    var delta = new Date().getTime() - date.getTime();
    var min = 60000;
    var hour = min * 60;
    if(delta < min) {
        return "moments ago";
    } else if(delta < 5 * min) {
        return "few minutes ago";
    } else if(delta < 15 * min) {
        return "ten minutes ago"
    } else if(delta < 40 * min) {
        return "half hour ago";
    } else if(delta < 90 * min) {
        return "hour ago"
    } else if(delta < 24 * hour) {
        return Math.round(delta / hour) + " hours ago";
    } else {
        return date.getFullYear() + "-" + leftPad(date.getMonth() + 1) + "-" + leftPad(date.getDate());
    }
}

function leftPad(str) {
    str+="";
    return str.length === 1 ? "0" + str : str;
}