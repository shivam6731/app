package com.foodpanda.urbanninja.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;


/**
 * Taking into account that each android component has there own lifecycle
 * we need to specify special one for {@link android.support.v4.app.Fragment}.
 * to use some components only in fragment context
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentScope {
}
