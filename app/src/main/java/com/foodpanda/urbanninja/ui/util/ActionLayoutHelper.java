package com.foodpanda.urbanninja.ui.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
    private boolean isEnableActionButton;
    // text of action button
    private CharSequence textActionButton;
    // icon left from the the text in the action button
    private Drawable drawableLeft;

    public ActionLayoutHelper(Context context) {
        this.context = context;
    }

    public void setActionButtonState() {
        updateActionButton(isActionButtonVisible,
            isEnableActionButton,
            textActionButton,
            drawableLeft);
    }

    public void saveActionButtonState() {
        isActionButtonVisible = layoutAction.getVisibility() == View.VISIBLE;
        isEnableActionButton = layoutAction.isEnabled();
        textActionButton = btnAction.getText();
        drawableLeft = btnAction.getCompoundDrawables()[0];
    }

    public void hideActionButton() {
        updateActionButton(false, false, 0, 0);
    }

    public void setReadyToWorkActionButton() {
        updateActionButton(true, true, R.string.action_ready_to_work, 0);
    }

    public void setRouteStopActionListButton(Stop stop) {
        int titleResourcesLink = stop.getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivered : R.string.action_at_picked_up;
        updateActionButton(true, stop.getActivities().isEmpty(), titleResourcesLink, R.drawable.arrow_swipe);
    }

    public void setDrivingHereStatusActionButton() {
        updateActionButton(true, true, R.string.action_driving, R.drawable.arrow_swipe);
    }

    public void setViewedStatusActionButton(Stop stop) {
        int title = stop.getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivery : R.string.action_at_pick_up;
        updateActionButton(true, true, title, R.drawable.arrow_swipe);
    }

    public void updateActionButton(boolean isEnabled, int textResourceLink) {
        updateActionButton(true, isEnabled, textResourceLink, 0);
    }

    private void updateActionButton(
        final boolean isVisible,
        final boolean isEnable,
        final int textResLink,
        final int drawableResLinkLeft
    ) {
        updateActionButton(
            isVisible,
            isEnable,
            textResLink == 0 ? "" : context.getResources().getString(textResLink),
            drawableResLinkLeft == 0 ? null : ContextCompat.getDrawable(context, drawableResLinkLeft));
    }

    private void updateActionButton(
        final boolean isVisible,
        final boolean isEnable,
        final CharSequence text,
        final Drawable drawableLeft
    ) {
        if (isVisible) {
            layoutAction.setVisibility(View.VISIBLE);
            layoutAction.setEnabled(isEnable);
            if (drawableLeft != null) {
                btnAction.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            }
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

    public void disableActionButton() {
        isEnableActionButton = false;
        layoutAction.setEnabled(isEnableActionButton);
    }
}
