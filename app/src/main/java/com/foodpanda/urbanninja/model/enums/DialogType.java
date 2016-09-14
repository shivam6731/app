package com.foodpanda.urbanninja.model.enums;

public enum DialogType {
    //it's just a type of dialog to show information without additional action
    INFORMATION,
    //this type redirect to the GPS setting
    GPS,
    //warning message after access will launch com.foodpanda.urbanninja.ui.dialog.IssueCollectedDialog
    ISSUE_COLLECTION_WARNING,
}
