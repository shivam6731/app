package com.foodpanda.urbanninja.model.enums;

import android.content.Context;

import com.foodpanda.urbanninja.R;

import java.util.LinkedList;
import java.util.List;

public enum CollectionIssueReason {

    VENDOR_MIXED_ITEMS(R.string.issue_collection_reason_vendor_mixed_items),
    ITEMS_NOT_AVAILABLE(R.string.issue_collection_reason_items_not_available),
    RIDER_DAMAGED_ITEM(R.string.issue_collection_reason_rider_damaged_item),
    RIDER_HAD_NO_MONEY_CHANGE(R.string.issue_collection_reason_vendor_mixed_items),
    DISCOUNT_GIVEN_FOR_LATENESS(R.string.issue_collection_reason_discount_given_for_lateness),
    RIDER_PAID_MORE_PRICE_MISMATCH(R.string.issue_collection_reason_rider_paid_more_price_mismatch),
    RIDER_PAID_LESS_PRICE_MISMATCH(R.string.issue_collection_reason_rider_paid_less_price_mismatch),
    ORDER_WAS_PARTIALLY_PAID_ONLINE(R.string.issue_collection_reason_order_was_partially_paid_online),
    RIDER_LOST_MONEY(R.string.issue_collection_reason_rider_lost_money),
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
