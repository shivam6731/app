package com.foodpanda.urbanninja.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Taking into account that each android component has there own lifecycle
 * we need to specify special one for {@link android.app.Activity}.
 * to use some components only in activity context
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {
}
