package com.foodpanda.urbanninja.di.component;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.receiver.ConnectionStatusReceiver;
import com.foodpanda.urbanninja.api.service.GcmListenerService;
import com.foodpanda.urbanninja.api.service.LocationService;
import com.foodpanda.urbanninja.api.service.RegistrationIntentService;
import com.foodpanda.urbanninja.di.module.TimerHelperAndOrderTypePaymentHelperModule;
import com.foodpanda.urbanninja.di.module.AppModule;
import com.foodpanda.urbanninja.di.module.MainActivityModule;
import com.foodpanda.urbanninja.di.module.OrderNestedFragmentModule;
import com.foodpanda.urbanninja.ui.activity.LoginActivity;
import com.foodpanda.urbanninja.ui.fragments.CashReportListFragment;
import com.foodpanda.urbanninja.ui.fragments.CountryListFragment;
import com.foodpanda.urbanninja.ui.fragments.LoginFragment;
import com.foodpanda.urbanninja.ui.fragments.ScheduleListFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * this is our singleton component and we store instance of this interface in {@link App} class,
 * after in each place where we need something to inject we add method that would be implemented by dagger.
 * For instance you can check {@link #inject(App)} method
 * <p/>
 * in case when we need to add some feature to extend our base component we use plus method,
 * for instance this one {@link #plus(MainActivityModule)}.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface MainComponent {
    void inject(LoginActivity loginActivity);

    void inject(App app);

    void inject(GcmListenerService gcmListenerService);

    void inject(LocationService locationService);

    void inject(LoginFragment loginFragment);

    void inject(RegistrationIntentService registrationIntentService);

    void inject(CashReportListFragment cashReportListFragment);

    void inject(CountryListFragment countryListFragment);

    void inject(ScheduleListFragment scheduleListFragment);

    void inject(ConnectionStatusReceiver connectionStatusReceiver);

    OrderNestedFragmentComponent plus(OrderNestedFragmentModule orderNestedFragmentModule);

    MainActivityComponent plus(MainActivityModule mainActivityModule);

    TimerHelperAndOrderTypePaymentHelperComponent plus(TimerHelperAndOrderTypePaymentHelperModule timerHelperAndOrderTypePaymentHelperModule);
}
