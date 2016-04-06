package com.foodpanda.urbanninja.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;

public class ActionLayoutHelper {
    private Context context;
    private Button btnAction;
    private View layoutAction;

    // state for action button in the bottom of the screen
    // we need to save it when this view would be recreated

    // is action button visible
    private boolean isActionButtonVisible = false;
    // is action button enable
    private boolean isEnabledActionButton;
    // text of action button
    private CharSequence textActionButton;
    // icon left from the the text in the action button

    public ActionLayoutHelper(Context context) {
        this.context = context;
    }

    public void setActionButtonState() {
        updateActionButton(isActionButtonVisible,
            isEnabledActionButton,
            textActionButton);
    }

    public void saveActionButtonState() {
        isActionButtonVisible = layoutAction.getVisibility() == View.VISIBLE;
        isEnabledActionButton = layoutAction.isEnabled();
        textActionButton = btnAction.getText();
    }

    public void hideActionButton() {
        updateActionButton(false, false, 0);
    }

    public void setEnabled(boolean isEnabled) {
        isEnabledActionButton = isEnabled;
        layoutAction.setEnabled(isEnabled);
    }

    public void setReadyToWorkActionButton() {
        updateActionButton(true, true, R.string.action_ready_to_work);
    }

    public void setRouteStopActionListButton(Stop stop) {
        int titleResourcesLink = stop.getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivered : R.string.action_at_picked_up;
        updateActionButton(true, stop.getActivities().isEmpty(), titleResourcesLink);
    }

    public void setDrivingHereStatusActionButton() {
        updateActionButton(true, true, R.string.action_driving);
    }

    public void setViewedStatusActionButton(Stop stop) {
        int title = stop.getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivery : R.string.action_at_pick_up;
        updateActionButton(true, false, title);
    }

    public void updateActionButton(boolean isEnabled, int textResourceLink) {
        updateActionButton(true, isEnabled, textResourceLink);
    }

    private void updateActionButton(
        final boolean isVisible,
        final boolean isEnable,
        final int textResLink
    ) {
        updateActionButton(
            isVisible,
            isEnable,
            textResLink == 0 ? "" : context.getResources().getString(textResLink));
    }

    private void updateActionButton(
        final boolean isVisible,
        final boolean isEnable,
        final CharSequence text
    ) {
        if (isVisible) {
            layoutAction.setVisibility(View.VISIBLE);
            layoutAction.setEnabled(isEnable);
            btnAction.setText(text);
        } else {
            layoutAction.setVisibility(View.GONE);
        }
    }

    public void setBtnAction(Button btnAction) {
        this.btnAction = btnAction;
    }

    public void setLayoutAction(View layoutAction) {
        this.layoutAction = layoutAction;
    }

}
