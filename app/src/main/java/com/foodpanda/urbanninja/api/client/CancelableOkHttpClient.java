package com.foodpanda.urbanninja.api.client;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

public class CancelableOkHttpClient extends OkHttpClient {
    public static final Object TAG_CALL = new Object();

    @Override
    public Call newCall(Request request) {
        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.tag(TAG_CALL);
        return super.newCall(requestBuilder.build());
    }
}
