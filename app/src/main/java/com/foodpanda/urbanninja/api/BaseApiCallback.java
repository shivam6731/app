package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.ErrorMessage;

public interface BaseApiCallback<T> {
    void onSuccess(T t);

    void onError(ErrorMessage errorMessage);
}
