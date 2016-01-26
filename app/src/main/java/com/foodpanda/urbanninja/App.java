package com.foodpanda.urbanninja;

import android.app.Application;

import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.StorageManager;

public class App extends Application {
    public static final ApiManager API_MANAGER = new ApiManager();
    public static final StorageManager STORAGE_MANAGER = new StorageManager();

    @Override
    public void onCreate() {
        super.onCreate();
        STORAGE_MANAGER.init(getApplicationContext());
        API_MANAGER.init(getApplicationContext());
    }
}
