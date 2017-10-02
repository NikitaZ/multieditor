/* 
 * Scripts used on *.xhtml pages
 * 
 * NOTE: common.js and sortable.js should be included before
 */

function applyDefaultSorting() {
    var tables = jQuery("table.sortable");
    var updateColoring = false;
    // ascending order
    jQuery("th.sorted-by-default", tables).each(function () {
        jQuery(this).click();
        updateColoring = true;
    });
    // descending order (requires extra click)
    jQuery("th.sorted-by-default.desc-order", tables).each(function () {
        jQuery(this).click();
        updateColoring = true;
    });

    if (updateColoring) {
        tables.each(function () {
            updateTableColoring(jQuery(this));
        });
    }
}

addOnloadAction(applyDefaultSorting);

// assume that itemsSelector selects checkboxes
function makeShiftSelectable(itemsSelector) {
    jQuery(itemsSelector).click(function (event) {
        if (event.shiftKey) { // 'Shift' key is pressed
            var items = jQuery(itemsSelector);
            // search for first selected checkbox
            var lastChecked = items.filter(":checked:eq(0)");

            if (lastChecked.length == 0) {
                lastChecked = event.target;
            }

            var start = items.index(lastChecked);
            var end = items.index(event.target) + 1;

            // do not use items.slice(start, end).attr('checked', ...)
            // because of docs at http://api.jquery.com/prop/
            items.slice(start, end).prop("checked", true);
        }
    });
};

function displayConfirmation(listId, alertMsg, actionMsg) {
    var listValue = jQuery(getIdSelector(listId)).val();
    if (!listValue) {
        alert("Please, select " + alertMsg + ".");
        return false;
    }
    return window.confirm("Are you sure you want to " + actionMsg + " with name '" + listValue + "'?");
}

function confirmDocumentRemove(listId) {
    return displayConfirmation(listId, "document to delete", "delete a document");
}