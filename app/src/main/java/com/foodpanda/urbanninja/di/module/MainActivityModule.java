package com.foodpanda.urbanninja.di.module;

import com.foodpanda.urbanninja.di.scope.ActivityScope;
import com.foodpanda.urbanninja.manager.LocationSettingCheckManager;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
    private MainActivity mainActivity;
    private NestedFragmentCallback nestedFragmentCallback;

    public MainActivityModule(MainActivity mainActivity, NestedFragmentCallback nestedFragmentCallback) {
        this.mainActivity = mainActivity;
        this.nestedFragmentCallback = nestedFragmentCallback;
    }

    @Provides
    @ActivityScope
    LocationSettingCheckManager providesCheckPolygonManager() {
        return new LocationSettingCheckManager(mainActivity, nestedFragmentCallback);
    }

}
