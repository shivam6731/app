package com.foodpanda.urbanninja.manager;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.api.rx.action.RetryAction;
import com.foodpanda.urbanninja.api.rx.subscriber.BackgroundSubscriber;
import com.foodpanda.urbanninja.model.enums.Status;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class ApiManagerTest {
    private ApiManager apiManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        apiManager.init(app);
        when(apiManager.getRiderObservable()).thenReturn(Observable.empty());
        when(apiManager.getCurrentScheduleObservable()).thenReturn(Observable.empty());
        when(apiManager.getRouteObservable(anyInt())).thenReturn(Observable.empty());

        apiExecutor = new ApiExecutor(activity, nestedFragmentCallback, apiManager, storageManager, multiPickupManager);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test

    public void notifyActionPerformed(long routeId, Status status) {
        PerformActionWrapper performActionWrapper = new PerformActionWrapper(status, new DateTime(), storageManager.getRiderLocation());

        compositeSubscription.add(
            wrapRetryObservable(
                service.notifyActionPerformed(routeId, performActionWrapper),
                new RetryAction(routeId, performActionWrapper)).
                subscribe(new BackgroundSubscriber<>()));
    }
}
