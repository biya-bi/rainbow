function handleSubmit(args, dialog) {
    var jqDialog = jQuery('#' + dialog);
    if (args.validationFailed) {
        jqDialog.effect('shake', {times: 3}, 100);
    } else {
        PF(dialog).hide();
    }
}

function getColumnsCount(args) {
    return   PF(args).getColumnsCount();
}

function showDialog(dialog) {
    if (dialog === ' ') {
        return;
    }
    PF(dialog).show();
}
function hideDialog(dialog) {
    if (dialog === ' ') {
        return;
    }
    PF(dialog).hide();
}
