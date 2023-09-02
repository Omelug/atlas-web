function confirmAction(confirmationMessage, title, logoUrl) {
    /*var confirmationDialog = $("<div class='custom-confirm-dialog'></div>");

    //var dialogTitle = title || "Confirm Action";
    //var dialogLogo = logoUrl || "/default-logo.png"; // Replace with your logo URL

    //confirmationDialog.append(`<div class="dialog-logo"><img src="${dialogLogo}" alt="Logo"></div>`);
    //confirmationDialog.append(`<h2 class="dialog-title">${dialogTitle}</h2>`);
    confirmationDialog.append(`<p class="dialog-message">${message}</p>`);
    confirmationDialog.append(`<div class="dialog-buttons">
    <button class="btn-confirm">Yes</button>
    <button class="btn-cancel">Cancel</button>
  </div>`);

    $("body").append(confirmationDialog);

    return new Promise(function (resolve, reject) {
        $(".btn-confirm").on("click", function () {
            confirmationDialog.remove();
            resolve(true);
        });

        $(".btn-cancel").on("click", function () {
            confirmationDialog.remove();
            resolve(false);
        });
    });*/

    return window.confirm(confirmationMessage);
}

