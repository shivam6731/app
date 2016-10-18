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
    ISSUE_CUSTOMER_SENDING
}
