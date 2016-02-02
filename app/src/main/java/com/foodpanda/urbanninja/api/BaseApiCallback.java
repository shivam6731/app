package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.model.Model;

/**
 * Result of each API request to react in a UI
 *
 * @param <T> extends {@link Model} as all our API results do
 */
public interface BaseApiCallback<T extends Model> {
    void onSuccess(T t);

    void onError(ErrorMessage errorMessage);
}
