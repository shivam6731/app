package com.foodpanda.urbanninja;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.foodpanda.urbanninja.di.component.DaggerMainComponent;
import com.foodpanda.urbanninja.di.component.MainComponent;
import com.foodpanda.urbanninja.di.module.AppModule;
import com.foodpanda.urbanninja.manager.LanguageManager;
import com.foodpanda.urbanninja.ui.activity.MainActivity;

import javax.inject.Inject;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private static boolean isInterestingActivityVisible;

    private MainComponent mainComponent;

    @Inject
    LanguageManager languageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        mainComponent = DaggerMainComponent.builder().appModule(new AppModule(this)).build();
        mainComponent.inject(this);
        setLanguage();
    }

    /**
     * Before each launch of the app we should force
     * to switch to the rider selected language
     */
    private void setLanguage() {
        languageManager.setLanguage(this);
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

    /**
     * to make you injection easy we need to get application context from each class related to android life cycle.
     * after from this instance component would be taken {@link #getMainComponent()}
     *
     * @param context to get application context from and cast to our custom implementation
     * @return casted to the {@link App} instance of application context
     */
    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    /**
     * in this component we store all dependency to out {@link AppModule}.
     * Dagger would generate code that implement {@link MainComponent} and provides all dependency
     *
     * @return dagger injection interface
     */
    public MainComponent getMainComponent() {
        return mainComponent;
    }
}
