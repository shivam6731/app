package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.model.Model;

/**
 * Child of {@link BaseApiCallback} that allows us know that
 * API request was stored to the {@link android.content.SharedPreferences}
 * to be executed as soon as internet connection or server would be available
 *
 * @param <T> extends {@link Model} as all our API results do
 */
public interface StorableApiCallback<T extends Model> extends BaseApiCallback<T> {
    void onItemStored();
}
