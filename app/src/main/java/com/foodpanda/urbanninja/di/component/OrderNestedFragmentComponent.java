package com.foodpanda.urbanninja.di.component;

import com.foodpanda.urbanninja.di.module.OrderNestedFragmentModule;
import com.foodpanda.urbanninja.di.scope.FragmentScope;
import com.foodpanda.urbanninja.ui.fragments.OrdersNestedFragment;

import dagger.Subcomponent;


/**
 * Our nested fragment component.
 * However this is just a sub-component for our main one.{@link MainComponent}
 * Here we as a module have {@link OrderNestedFragmentModule},
 * this module provides all dependency that we need to create all instances of injected classes.
 * <p/>
 * as was mentioned in {@link MainComponent} in this interface we should have methods for each places
 * where this component should be injected.
 * As component only for specific class {@link OrdersNestedFragment}
 * we have only one method.
 */
@FragmentScope
@Subcomponent(modules = {OrderNestedFragmentModule.class})
public interface OrderNestedFragmentComponent {
    void inject(OrdersNestedFragment ordersNestedFragment);
}
