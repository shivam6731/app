package com.foodpanda.urbanninja.model.enums;

public enum DialogType {
    //it's just a type of dialog to show information without additional action
    INFORMATION,
    //this type redirect to the GPS setting
    GPS,
    //warning message after access will launch com.foodpanda.urbanninja.ui.dialog.IssueCollectedDialog
    ISSUE_COLLECTION_WARNING,
    //chooce dialog to open that kind of problem rider has with an order
    ISSUE_VENDOR_CUSTOMER_SELECTION,
    //open web view to fill out vendor issue form
    ISSUE_VENDOR_SENDING,
    //open web view to fill out customer issue form
    ISSUE_CUSTOMER_SENDING,
    //open developer options disable sending fake location
    FAKE_LOCATION_SETTING,
    //open web page to download latest version of the app
    NOT_UP_TO_DATE_APP_VERSION;

    /**
     * for some cases our dialog should not be cancelable, to force riders to do some actions
     * <p/>
     * Cancelable means that it's impossible to close it with back button.
     *
     * @param dialogType type of dialog to check
     * @return true if dialog should be cancelable
     */
    public static boolean isDialogCancelable(DialogType dialogType) {
        return !(dialogType == DialogType.FAKE_LOCATION_SETTING || dialogType == DialogType.NOT_UP_TO_DATE_APP_VERSION);
    }
}
