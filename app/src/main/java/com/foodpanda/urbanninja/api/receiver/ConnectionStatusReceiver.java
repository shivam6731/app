package com.foodpanda.urbanninja.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.manager.ApiManager;

public class ConnectionStatusReceiver extends BroadcastReceiver {
    private ApiManager apiManager;

    public ConnectionStatusReceiver() {
        super();
        apiManager = App.API_MANAGER;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isInternetConnectionAvailable(context)) {
            apiManager.sendAllFailedRequests();
        }
    }

    boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }


}
