package com.foodpanda.urbanninja.di.component;

import com.foodpanda.urbanninja.di.module.MainActivityModule;
import com.foodpanda.urbanninja.di.scope.ActivityScope;
import com.foodpanda.urbanninja.ui.activity.MainActivity;

import dagger.Subcomponent;

/**
 * Our main activity component.
 * However this is just a sub-component for our main one.{@link MainComponent}
 * Here we as a module have {@link MainActivityModule},
 * this module provides all dependencies that we need to create all instances of injected classes.
 * <p/>
 * as was mentioned in {@link MainComponent} in this interface we should have methods for each places
 * where this component should be injected.
 * As component only for specific class {@link MainActivity}
 * we have only one method.
 */
@ActivityScope
@Subcomponent(modules = {MainActivityModule.class})
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
}
