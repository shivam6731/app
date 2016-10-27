package com.foodpanda.urbanninja.di.module;

import com.foodpanda.urbanninja.di.scope.FragmentScope;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.ui.fragments.BaseFragment;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerHelperAndOrderTypePaymentHelperModule {
    private BaseActivity baseActivity;
    private BaseFragment baseFragment;
    private TimerDataProvider timerDataProvider;
    private NestedFragmentCallback nestedFragmentCallback;

    public TimerHelperAndOrderTypePaymentHelperModule(
        BaseActivity baseActivity,
        BaseFragment baseFragment,
        TimerDataProvider timerDataProvider,
        NestedFragmentCallback nestedFragmentCallback
    ) {
        this(baseActivity, baseFragment, timerDataProvider);
        this.nestedFragmentCallback = nestedFragmentCallback;
    }

    public TimerHelperAndOrderTypePaymentHelperModule(BaseActivity baseActivity, BaseFragment baseFragment, TimerDataProvider timerDataProvider) {
        this.baseActivity = baseActivity;
        this.baseFragment = baseFragment;
        this.timerDataProvider = timerDataProvider;
    }

    @Provides
    @FragmentScope
    BaseActivity providesBaseActivity() {
        return baseActivity;
    }

    @Provides
    @FragmentScope
    TimerHelper providesTimerHelper() {
        return new TimerHelper(baseActivity, baseFragment, timerDataProvider, nestedFragmentCallback);
    }

}
