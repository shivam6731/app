package com.foodpanda.urbanninja.di.module;

import android.content.Context;

import com.foodpanda.urbanninja.manager.StorageManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    StorageManager providesStorageManager() {
        return new StorageManager(context);
    }
}
