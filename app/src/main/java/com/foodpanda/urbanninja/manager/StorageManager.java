package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.api.model.StorableStatus;
import com.foodpanda.urbanninja.api.serializer.DateTimeDeserializer;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.TokenData;
import com.foodpanda.urbanninja.model.enums.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class StorageManager implements Managable {
    private SharedPreferences sharedPreferences;
    private SharedPreferences cachedRequestPreferences;
    private Gson gson;

    private Token token;
    private List<Stop> stopList = new LinkedList<>();
    private Map<Long, Status> stopStatusMap = new LinkedHashMap<>();

    @Override
    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        cachedRequestPreferences = context.getSharedPreferences(Constants.Preferences.CACHED_REQUESTS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        gson = new GsonBuilder().
            registerTypeAdapter(DateTime.class, new DateTimeDeserializer()).
            create();
        setStatusMap();
    }

    public boolean storeToken(Token token) {
        this.token = token;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(token);
        editor.putString(Constants.Preferences.TOKEN, json);

        return editor.commit();
    }

    public Token getToken() {
        if (token == null) {
            String json = sharedPreferences.getString(Constants.Preferences.TOKEN, "");
            token = gson.fromJson(json, Token.class);
        }

        return token;
    }

    public TokenData getTokenData() {
        if (getToken() != null && !TextUtils.isEmpty(getToken().getAccessToken())) {
            byte[] bytes = getToken().getAccessToken().split("\\.")[1].getBytes();
            byte[] valueDecoded = Base64.decode(bytes, Base64.DEFAULT);

            return gson.fromJson(new String(valueDecoded), TokenData.class);
        } else {

            return new TokenData();
        }
    }

    public boolean storeUserName(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.Preferences.USERNAME, username);

        return editor.commit();
    }

    public String getUsername() {
        return sharedPreferences.getString(Constants.Preferences.USERNAME, "");
    }

    public boolean storePassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.Preferences.PASSWORD, password);

        return editor.commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(Constants.Preferences.PASSWORD, "");
    }

    public boolean storeCountry(Country country) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(country);
        editor.putString(Constants.Preferences.COUNTRY, json);

        return editor.commit();
    }

    /**
     * Clean all stored data
     */
    public void cleanSession() {
        storeToken(null);
        stopStatusMap.clear();
        stopList.clear();
    }

    public Country getCountry() {
        String json = sharedPreferences.getString(Constants.Preferences.COUNTRY, "");

        return gson.fromJson(json, Country.class);
    }

    public void storeStopList(List<Stop> stopList) {
        this.stopList = getUpToDateList(stopList);
        cleanStatusMap();
    }

    public List<Stop> getStopList() {
        return stopList == null ? new LinkedList<Stop>() : stopList;
    }

    public boolean storeStatus(long routeId, Status status) {
        stopStatusMap.put(routeId, status);
        String json = gson.toJson(stopStatusMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.Preferences.STATUS_LIST, json);

        return editor.commit();
    }

    public Stop getCurrentStop() {
        if (stopList.isEmpty()) {
            return null;
        } else {
            return stopList.get(0);
        }
    }

    public Stop removeCurrentStop() {
        if (stopList.isEmpty()) {
            return null;
        }
        Stop stop = stopList.remove(0);
        getUpToDateList(stopList);

        return stop;
    }

    public boolean storeStatusApiRequests(Queue<StorableStatus> requestsQueue) {
        SharedPreferences.Editor editor = cachedRequestPreferences.edit();
        String json = gson.toJson(requestsQueue);
        editor.putString(Constants.Preferences.STATUS_REQUEST_LIST, json);

        return editor.commit();
    }

    public Queue<StorableStatus> getStatusApiRequestList() {
        Queue<StorableStatus> requestsQueue = new LinkedList<>();
        String json = cachedRequestPreferences.getString(Constants.Preferences.STATUS_REQUEST_LIST, "");
        Queue<StorableStatus> calls = gson.fromJson(json, new TypeToken<Queue<StorableStatus>>() {
        }.getType());

        return calls != null ? calls : requestsQueue;
    }

    public boolean storeLocationApiRequests(Queue<RiderLocation> requestsQueue) {
        SharedPreferences.Editor editor = cachedRequestPreferences.edit();
        String json = gson.toJson(requestsQueue);
        editor.putString(Constants.Preferences.LOCATION_REQUEST_LIST, json);

        return editor.commit();
    }

    public Queue<RiderLocation> getLocationApiRequestList() {
        Queue<RiderLocation> requestsQueue = new LinkedList<>();
        String json = cachedRequestPreferences.getString(Constants.Preferences.LOCATION_REQUEST_LIST, "");
        Queue<RiderLocation> calls = gson.fromJson(json, new TypeToken<Queue<RiderLocation>>() {
        }.getType());

        return calls != null ? calls : requestsQueue;
    }

    public boolean storeVehicleId(int vehicleId) {
        SharedPreferences.Editor editor = cachedRequestPreferences.edit();
        editor.putInt(Constants.Preferences.VEHICLE_ID, vehicleId);

        return editor.commit();
    }

    public int getVehicleId() {
        return cachedRequestPreferences.getInt(Constants.Preferences.VEHICLE_ID, 0);
    }

    private List<Stop> getUpToDateList(List<Stop> stopList) {
        for (Stop stop : stopList) {
            if (stopStatusMap.get(stop.getId()) != null) {
                stop.setStatus(stopStatusMap.get(stop.getId()));
            }
        }

        return removeCompletedStops(stopList);
    }

    private boolean cleanStatusMap() {
        stopStatusMap.clear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.Preferences.STATUS_LIST, "");

        return editor.commit();
    }

    private void setStatusMap() {
        String json = sharedPreferences.getString(Constants.Preferences.STATUS_LIST, "");
        if (!TextUtils.isEmpty(json)) {
            stopStatusMap = gson.fromJson(json, new TypeToken<LinkedHashMap<Long, Status>>() {
            }.getType());
        }
    }

    private List<Stop> removeCompletedStops(List<Stop> stopList) {
        for (Iterator<Stop> iterator = stopList.iterator(); iterator.hasNext(); ) {
            Stop stop = iterator.next();
            if (stop.getStatus() == Status.CANCELED ||
                stop.getStatus() == Status.COMPLETED) {
                iterator.remove();
            }
        }

        return stopList;
    }
}
