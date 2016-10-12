package com.foodpanda.urbanninja.model.enums;

import android.content.Context;

import com.foodpanda.urbanninja.R;

import java.util.LinkedList;
import java.util.List;

public enum CollectionIssueReason {

    WRONG_ITEMS(R.string.issue_collection_reason_wrong_items),
    MISSING_ITEMS(R.string.issue_collection_reason_missing_items),
    DAMAGED_ITEM(R.string.issue_collection_reason_damaged_items),
    OTHER(R.string.issue_collection_reason_other);

    private int resourceId;

    CollectionIssueReason(int id) {
        resourceId = id;
    }

    public String resource(Context context) {
        return context.getString(resourceId);
    }

    public static List<String> convertToTranslationList(Context context) {
        List<String> reasons = new LinkedList<>();
        for (CollectionIssueReason collectionIssueReason : values()) {
            reasons.add(collectionIssueReason.resource(context));
        }

        return reasons;
    }
}
