package com.foodpanda.urbanninja.di.component;


import com.foodpanda.urbanninja.di.module.TimerHelperAndOrderTypePaymentHelperModule;
import com.foodpanda.urbanninja.di.scope.FragmentScope;
import com.foodpanda.urbanninja.ui.fragments.ReadyToWorkFragment;
import com.foodpanda.urbanninja.ui.fragments.RouteStopActionListFragment;
import com.foodpanda.urbanninja.ui.fragments.RouteStopDetailsFragment;

import dagger.Subcomponent;

/**
 * Our time and payment component.
 * We use it in each place there we have timer view or/and payment view.
 * However this is just a sub-component for our main one.{@link MainComponent}
 * Here we as a module have {@link TimerHelperAndOrderTypePaymentHelperModule},
 * this module provides all dependency that we need to create all instances of injected classes.
 * <p/>
 * as was mentioned in {@link MainComponent} in this interface we should have methods for each places
 * where this component should be injected.
 */
@FragmentScope
@Subcomponent(modules = {TimerHelperAndOrderTypePaymentHelperModule.class})
public interface TimerHelperAndOrderTypePaymentHelperComponent {

    void inject(ReadyToWorkFragment readyToWorkFragment);

    void inject(RouteStopDetailsFragment routeStopDetailsFragment);

    void inject(RouteStopActionListFragment routeStopActionListFragment);
}
