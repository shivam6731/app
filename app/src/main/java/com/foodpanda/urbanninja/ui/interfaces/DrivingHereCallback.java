package com.foodpanda.urbanninja.ui.interfaces;

/**
 * Used for receiving notifications when user status had been changed to
 * {@link com.foodpanda.urbanninja.model.enums.Action#ON_THE_WAY}
 * and done check box should be visible
 */
public interface DrivingHereCallback {

    /**
     * When driver at status that is not {@link com.foodpanda.urbanninja.model.enums.Action#ON_THE_WAY}
     * checkbox should be not visible
     *
     * @param isVisible set visibility
     */
    void changeActionDoneCheckboxVisibility(boolean isVisible);

}
