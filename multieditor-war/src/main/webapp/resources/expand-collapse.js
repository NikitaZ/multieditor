/*
    functions used for show/hide in impl of gc:expand-collapse component 
 */
function ExpandCollapse(id) {
    if (document.getElementById(id + ":Body").style.display == 'block')
        Collapse(id);
    else
        Expand(id);
    return false; // do not submit
}

function Collapse(id) {
    var e = document.getElementById(id + ":Body");
    e.style.display = 'none';
    document.getElementById(id + ":Collapse").style.display = "none";
    document.getElementById(id + ":Expand").style.display = "inline";
}

function Expand(id) {
    var e = document.getElementById(id + ":Body");
    e.style.display = 'block';
    document.getElementById(id + ":Collapse").style.display = "inline";
    document.getElementById(id + ":Expand").style.display = "none";
}

function Hide(id) {
    document.getElementById(id + ":Body").style.display = "none";
}

function Show(id) {
    document.getElementById(id + ":Body").style.display = "inline";
}

/* functions used in gc:showHideControl and gc:showHidePanel */

function showHide(name) {
    showHideEx(name, null, false);
}

// showHide extended (supports AJAX)
function showHideEx(name, source, useAjax) {
    var list = document.getElementsByName(name);
    var className = "hidden-item";

    for (var i = 0; i < list.length; ++i) {
        if (list[i].className.toString().indexOf(className) >= 0) {
            removeClass(list[i], className);
            if (useAjax && list[i].tagName == "DIV") {
                jQuery("input[type='submit']" + getClassAttrSelector(source)).click();
            }
        } else {
            addClass(list[i], className);
        }
    }
}

var statusBarElem = null;

function ajaxShowHide(name, source, statusBarId) {
    statusBarElem = jQuery(getIdSelector(statusBarId));
    showHideEx(name, source, true);
}

function showExpandProgress(data) {
    if (data.status == "begin") {
        statusBarElem.show();
    }
    if (data.status == "success") {
        statusBarElem.hide();
    }
}

function removeClass(elem, clName) {
    var s = elem.className.toString();
    s = s.replace(clName, '');
    elem.className = s;
}

function addClass(elem, clName) {
    elem.className += " " + clName;
}
