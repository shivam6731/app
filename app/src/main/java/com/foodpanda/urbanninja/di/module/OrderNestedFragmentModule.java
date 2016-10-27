package com.foodpanda.urbanninja.di.module;

import android.content.Context;

import com.foodpanda.urbanninja.di.scope.FragmentScope;
import com.foodpanda.urbanninja.manager.CheckPolygonManager;
import com.foodpanda.urbanninja.manager.LocationSettingCheckManager;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import dagger.Module;
import dagger.Provides;

@Module
public class OrderNestedFragmentModule {
    private MainActivity mainActivity;
    private NestedFragmentCallback nestedFragmentCallback;

    public OrderNestedFragmentModule(MainActivity mainActivity, NestedFragmentCallback nestedFragmentCallback) {
        this.mainActivity = mainActivity;
        this.nestedFragmentCallback = nestedFragmentCallback;
    }

    @Provides
    @FragmentScope
    MainActivity providesMainActivity() {
        return mainActivity;
    }

    @Provides
    @FragmentScope
    NestedFragmentCallback providesNestedFragmentCallback() {
        return nestedFragmentCallback;
    }

    @Provides
    @FragmentScope
    CheckPolygonManager providesCheckPolygonManager() {
        return new CheckPolygonManager(mainActivity);
    }

    @Provides
    @FragmentScope
    LocationSettingCheckManager providesLocationSettingCheckManager() {
        return new LocationSettingCheckManager(mainActivity, nestedFragmentCallback);
    }

    @Provides
    @FragmentScope
    Context providesContext() {
        return mainActivity;
    }

}
