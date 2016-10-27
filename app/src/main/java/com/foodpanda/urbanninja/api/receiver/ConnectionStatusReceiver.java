package com.foodpanda.urbanninja.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.manager.ApiManager;

import javax.inject.Inject;

public class ConnectionStatusReceiver extends BroadcastReceiver {
    @Inject
    ApiManager apiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.get(context).getMainComponent().inject(this);

        if (ConnectivityManager.CONNECTIVITY_ACTION.equalsIgnoreCase(intent.getAction()) &&
            isInternetConnectionAvailable(context)) {
            apiManager.sendAllFailedRequests();
        }
    }

    boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }


}
