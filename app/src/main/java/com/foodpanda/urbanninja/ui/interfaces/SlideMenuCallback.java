package com.foodpanda.urbanninja.ui.interfaces;

/**
 * Interface describe navigation menu items
 */
public interface SlideMenuCallback {
    /**
     * Orders section with information about current order or
     * clock-in screen inside
     */
    void onOrdersClicked();

    /**
     * Rider schedule section with list of future working day
     */
    void onScheduleClicked();

    /**
     * Rider working report section with all worked days
     * and total revenue for each day
     */
    void onCashReportClicked();

    /**
     * Logout feature
     * Rider will be redirected to the {@link com.foodpanda.urbanninja.ui.activity.LoginActivity}
     */
    void onLogoutClicked();
}
