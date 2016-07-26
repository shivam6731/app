package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.RouteStopActivity;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.MapPointType;
import com.foodpanda.urbanninja.model.enums.RouteStopActivityType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsCallback;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsChangeListener;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.OrderTypeAndPaymentHelper;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import org.joda.time.DateTime;

public class RouteStopDetailsFragment extends BaseFragment implements
    TimerDataProvider,
    MapAddressDetailsChangeListener,
    MapAddressDetailsCallback {
    private static final int POSITION_FOR_HALAL_LAYOUT = 1;

    private NestedFragmentCallback nestedFragmentCallback;
    private TimerHelper timerHelper;

    private LinearLayout layoutContent;
    private RelativeLayout layoutTypeAndPayment;
    private TextView txtTimer;

    private Stop currentStop;

    private MapAddressDetailsChangeListener mapAddressDetailsChangeListener;

    public static RouteStopDetailsFragment newInstance(Stop stop) {
        RouteStopDetailsFragment routeStopDetailsFragment = new RouteStopDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.STOP, stop);
        routeStopDetailsFragment.setArguments(bundle);

        return routeStopDetailsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestedFragmentCallback = (NestedFragmentCallback) getParentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentStop = getArguments().getParcelable(Constants.BundleKeys.STOP);
        timerHelper = new TimerHelper(activity, this, this);
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.route_stop_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutContent = (LinearLayout) view.findViewById(R.id.layout_content);
        layoutTypeAndPayment = (RelativeLayout) view.findViewById(R.id.layout_type_payment);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);

        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // this fragment in a child view for some main container with
            // swipe to refresh logic inside, however here we have own scroll and to get rid of scroll conflict
            // we make main swipe view disable until we reach the top of the view here
            if (nestedFragmentCallback != null) {
                nestedFragmentCallback.setSwipeToRefreshEnable(scrollView.getScrollY() == 0);
            }
        });

        setData();
    }

    @Override
    public void onStart() {
        super.onStart();
        timerHelper.setTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        timerHelper.stopTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enableSwipeToRefreshDeleteCallback();
    }

    /**
     * Here we set all information about the current route currentStop for the text field
     * we have the same text view for the address and comment so we need to check if this data
     * present before set it
     */
    private void setData() {
        //Set payment details and type of the task
        new OrderTypeAndPaymentHelper(activity, currentStop, App.STORAGE_MANAGER).setType(layoutTypeAndPayment);

        //Launch the map details fragment
        MapAddressDetailsFragment mapAddressDetailsFragment = MapAddressDetailsFragment.newInstance(
            currentStop,
            currentStop.getTask() == RouteStopTask.DELIVER ? MapPointType.DELIVERY : MapPointType.PICK_UP,
            true);

        mapAddressDetailsChangeListener = mapAddressDetailsFragment;

        addFragment(R.id.map_details_container, mapAddressDetailsFragment);
        setHalalLayoutIfNeeds();
    }

    /**
     * Main main container swipe view enable and delete callback
     * We need to delete callback because {@link ViewTreeObserver.OnScrollChangedListener}
     * calls after {@link #onDestroy()} called and it means that we would have a wrong state for swipe view
     */
    private void enableSwipeToRefreshDeleteCallback() {
        if (nestedFragmentCallback != null) {
            nestedFragmentCallback.setSwipeToRefreshEnable(true);
            nestedFragmentCallback = null;
        }
    }

    /**
     * In some countries we support halal orders
     * and to let rider know to what bag he should put order
     * we need to add this layout
     */
    private void setHalalLayoutIfNeeds() {
        if (currentStop.getActivities() != null) {
            for (RouteStopActivity routeStopActivity : currentStop.getActivities()) {
                if (routeStopActivity.getType() == RouteStopActivityType.HALAL ||
                    routeStopActivity.getType() == RouteStopActivityType.NON_HALAL) {
                    addHalalLayout(putContentForHalalLayout(routeStopActivity.getType()));
                }
            }
        }
    }

    /**
     * set background for the whole halal layout
     * set background for header layout
     * set title and description for halal order
     *
     * @param routeStopActivityType type of halal order
     * @return halalLayout
     */
    private View putContentForHalalLayout(RouteStopActivityType routeStopActivityType) {
        View view = View.inflate(activity, R.layout.route_stop_details_halal_layout, null);

        View layoutHalal = view.findViewById(R.id.layout_halal_content);
        View layoutHeaderHalal = view.findViewById(R.id.layout_halal_header);
        ImageView imageHalalAlert = (ImageView) view.findViewById(R.id.image_halal_icon);
        TextView txtHalalName = (TextView) view.findViewById(R.id.txt_halal_title);
        TextView txtHalalDescription = (TextView) view.findViewById(R.id.txt_halal_description);

        boolean isHalal = routeStopActivityType == RouteStopActivityType.HALAL;

        layoutHalal.setBackgroundColor(ContextCompat.getColor(
            activity, isHalal ? R.color.halal_background_color : R.color.not_halal_background_color));

        layoutHeaderHalal.setBackgroundColor(ContextCompat.getColor(
            activity, isHalal ? R.color.green_text_color : R.color.toolbar_color));

        imageHalalAlert.setImageResource(isHalal ? R.drawable.icon_alert_green : R.drawable.icon_alert_red);
        txtHalalName.setText(isHalal ? R.string.route_action_halal : R.string.route_action_not_halal);
        txtHalalDescription.setText(isHalal ? R.string.task_details_halal : R.string.task_details_not_halal);

        return view;
    }

    /**
     * add layout with information about halal order
     *
     * @param view halal layout
     */
    private void addHalalLayout(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(
            getResources().getDimensionPixelSize(R.dimen.margin_card),
            getResources().getDimensionPixelSize(R.dimen.margin_card),
            getResources().getDimensionPixelSize(R.dimen.margin_card),
            getResources().getDimensionPixelSize(R.dimen.margin_card));

        layoutContent.addView(view, POSITION_FOR_HALAL_LAYOUT, layoutParams);
    }

    @Override
    public TextView provideTimerTextView() {
        return txtTimer;
    }

    @Override
    public DateTime provideScheduleDate() {
        return currentStop.getArrivalTime();
    }

    @Override
    public DateTime provideScheduleEndDate() {
        return currentStop.getArrivalTime().plusDays(1);
    }

    @Override
    public int provideActionButtonString() {
        return 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mapAddressDetailsChangeListener != null) {
            mapAddressDetailsChangeListener.onLocationChanged(location);
        }
    }

    @Override
    public void setActionDoneCheckboxVisibility(boolean isVisible) {
        if (mapAddressDetailsChangeListener != null) {
            mapAddressDetailsChangeListener.setActionDoneCheckboxVisibility(isVisible);
        }
    }

    @Override
    public void setActionButtonVisible(boolean isVisible) {
        nestedFragmentCallback.setActionButtonVisible(isVisible);
    }

    @Override
    public void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel) {
        nestedFragmentCallback.onSeeMapClicked(geoCoordinate, pinLabel);
    }

    @Override
    public void onPhoneNumberClicked(String phoneNumber) {
        nestedFragmentCallback.onPhoneNumberClicked(phoneNumber);
    }
}
