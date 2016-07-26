package com.foodpanda.urbanninja;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.LanguageManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.ui.activity.MainActivity;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    public static final ApiManager API_MANAGER = new ApiManager();
    public static final StorageManager STORAGE_MANAGER = new StorageManager();

    private static boolean isInterestingActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        STORAGE_MANAGER.init(getApplicationContext());
        API_MANAGER.init(getApplicationContext());
        registerActivityLifecycleCallbacks(this);
        setLanguage();
    }

    /**
     * Before each launch of the app we should force
     * to switch to the rider selected language
     */
    private void setLanguage() {
        new LanguageManager(STORAGE_MANAGER).setLanguage(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = false;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public static boolean isInterestingActivityVisible() {
        return isInterestingActivityVisible;
    }
}
