function addOnloadAction(action) {
    var prevAction = window.onload;

    window.onload = function () {
        if (prevAction) {
            prevAction();
        }
        action();
    }
}

function addUnloadAction(action) {
    var prevAction = window.onunload;

    window.onunload = function () {
        if (prevAction) {
            prevAction();
        }
        action();
    }
}

function filter(phrase, elemId) {
    var words = phrase.value.toLowerCase().split(" ");
    // select all table rows (except head and footer rows)
    jQuery(getIdSelector(elemId) + "> tbody > tr")
        .each(function () {
            var line = this.innerHTML.replace(/<[^>]+>/g, "").toLowerCase();
            this.style.display = phraseMatches(words, line) ? '' : 'none';
        });
}

function phraseMatches(words, line) {
    // all words should match
    for (var i = 0; i < words.length; i++) {
        var match = false;
        var wrds = words[i].split("|");
        for (var j = 0; j < wrds.length; j++) {
            var wrd = wrds[j];
            var negated = wrd.charAt(0) == '~';
            if (negated) {
                wrd = wrd.substring(1);
            }
            var contains = (line.indexOf(wrd) >= 0);
            if ((contains && !negated) || (negated && !contains)) {
                match = true;
            }
        }
        if (!match) {
            return false;
        }
    }
    return true;
}

addOnloadAction(
    function () {
        // saving table filtering after refresh
        var classname = "sortable";
        var nodes = document.getElementsByTagName("body")[0].getElementsByTagName("table");
        var re = new RegExp("\\b" + classname + "\\b");
        for (var i = 0; i < nodes.length; ++i) {
            if (re.test(nodes[i].className)) {
                var inputNodes
                    = nodes[i].getElementsByTagName("caption")[0].getElementsByTagName("input");

                var tFilter = inputNodes && inputNodes[0].id.indexOf("app-table-filter") > 0
                    ? inputNodes[0] : null;
                if (tFilter && tFilter.onkeyup) {
                    try {
                        tFilter.onkeyup.apply(tFilter);
                    } catch (e) {/*alert(e);*/
                    }
                }
            }
        }
    }
);

function getPerformanceData() {
    if (!window.performance || !window.performance.timing) {
        return "No data (may be, browser does not support Navigation Timing API)";
        /* Navigation Timing API (link to spec - look at Links section) and Mozilla support:
           https://developer.mozilla.org/en-US/docs/Navigation_timing

           IE support: http://msdn.microsoft.com/en-us/library/ie/hh673552%28v=vs.85%29.aspx
        */
    }
    var perfData = window.performance.timing;

    var text = "Server response time = "
        + (perfData.responseEnd - perfData.requestStart) + " ms\n";

    text += "Page download time = " + (perfData.responseEnd - perfData.responseStart) + " ms\n";
    text += "Page rendering on the client (including JS) = "
        + (perfData.loadEventEnd - perfData.responseEnd) + " ms\n";
    text += "Total page load time (including JS) = "
        + (perfData.loadEventEnd - perfData.navigationStart) + " ms";

    return text;
}

/* Returns CSS-selector by element id (with proper escaping) */
function getIdSelector(elementId) {
    return "#" + elementId.replace(/(:|\.|\[|\])/g, "\\$1");
}

/* Returns CSS-selector by the given part of the element id */
function getIdPartSelector(idPart) {
    return "[id*='" + idPart + "']";
}

/* Returns CSS-selector by the given style class name */
function getClassAttrSelector(className) {
    return "[class~='" + className + "']";
}

function defaultEnterHandler(event) {
    if (event.keyCode == 13 && !focusInTextArea()) {
        // try to find 'Refresh' button (it should not have class 'not-default-action')
        var refresh = jQuery("input[type='submit'][value='Refresh']:not([class~='not-default-action'])");

        if (refresh.length != 0) { // 'Refresh' button found
            refresh.click();
            return true;
        } else { // do nothing (do not submit)
            return false;
        }
    }
}

function focusInTextArea() {
    var focusedElem = document.activeElement;
    if (focusedElem && focusedElem.tagName == "TEXTAREA") {
        return true;
    }
    return false;
}

/** In case of 'Enter' key, press button with the given part of id */
function handleEnterKey(event, buttonIdPart) {
    /* check code of 'Enter' key */
    if (event.keyCode == 13) {
        jQuery(getIdPartSelector(buttonIdPart)).click();
    }
}

function setText(elemId, textValue) {
    jQuery(getIdSelector(elemId)).text(textValue);
}

function getTableVisibleRowsCnt(tableId) {
    var rows = jQuery("table" + getIdSelector(tableId) + " > tbody > tr").filter(":visible");
    return rows.length;
}

function updateTableColoringById(tableId) {
    updateRowColoring(jQuery("table" + getIdSelector(tableId) + " > tbody > tr"));
}

function updateTableColoring(tableElem) {
    // select only children rows (to ignore nested tables)
    updateRowColoring(jQuery(tableElem).children("tbody > tr"));
}

function updateRowColoring(rows) {
    jQuery(rows).filter(":visible")
        .each(function (index) {
            var row = jQuery(this);
            if ((index + 1) % 2 == 0) {
                row.removeClass("odd-row");
                row.addClass("even-row");
            } else {
                row.removeClass("even-row");
                row.addClass("odd-row");
            }
        });
}
