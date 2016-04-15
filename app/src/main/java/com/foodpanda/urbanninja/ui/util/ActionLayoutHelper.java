package com.foodpanda.urbanninja.ui.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    // text of action button
    private CharSequence textActionButton;
    // icon left from the the text in the action button

    public ActionLayoutHelper(Context context) {
        this.context = context;
    }

    public void setActionButtonState() {
        updateActionButton(isActionButtonVisible,
            textActionButton);
    }

    public void saveActionButtonState() {
        isActionButtonVisible = layoutAction.getVisibility() == View.VISIBLE;
        textActionButton = btnAction.getText();
    }

    public void hideActionButton() {
        updateActionButton(false, 0);
    }

    public void setVisibility(boolean isVisible) {
        showHideActionButtonWithAnimation(isVisible);
    }

    public void setReadyToWorkActionButton() {
        updateActionButton(true, R.string.action_ready_to_work);
    }

    public void setRouteStopActionListButton(Stop stop) {
        int titleResourcesLink = stop.getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivered : R.string.action_at_picked_up;
        updateActionButton(stop.getActivities().isEmpty(), titleResourcesLink);
    }

    public void setDrivingHereStatusActionButton() {
        updateActionButton(true, R.string.action_driving);
    }

    public void setViewedStatusActionButton(Stop stop) {
        int title = stop.getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivery : R.string.action_at_pick_up;
        updateActionButton(false, title);
    }

    public void updateActionButton(boolean isVisible, int textResourceLink) {
        updateActionButton(
            isVisible,
            textResourceLink == 0 ? "" : context.getResources().getString(textResourceLink));
    }

    private void updateActionButton(
        final boolean isVisible,
        final CharSequence text
    ) {

        if (isVisible != isActionButtonVisible) {
            setVisibility(isVisible);
        } else {
            layoutAction.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
        btnAction.setText(text);
        isActionButtonVisible = isVisible;
    }

    /**
     * Instead of making button disable we decided to hide or show it with animation
     *
     * @param isVisible show is view should be shown or hidden with animation
     */
    public void showHideActionButtonWithAnimation(final boolean isVisible) {
        Animation animation = AnimationUtils.loadAnimation(context, isVisible ? R.anim.bottom_up : R.anim.bottom_down);

        //we should disable button when animation in action to get rid of accidental clicks
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layoutAction.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutAction.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                layoutAction.setEnabled(true);
                layoutAction.invalidate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layoutAction.startAnimation(animation);
    }

    public void setBtnAction(Button btnAction) {
        this.btnAction = btnAction;
    }

    public void setLayoutAction(View layoutAction) {
        this.layoutAction = layoutAction;
    }

}
