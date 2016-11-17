package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.api.model.StorableStatus;
import com.foodpanda.urbanninja.api.serializer.DateTimeDeserializer;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.Language;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.TokenData;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.model.enums.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StorageManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences cachedRequestPreferences;
    private Gson gson;

    private Token token;
    private List<Stop> stopList = new LinkedList<>();
    private Map<Long, Status> stopStatusMap = new LinkedHashMap<>();
    private RiderLocation riderLocation;
    private Rider rider;

    @Inject
    public StorageManager(Context context) {
        init(context);
    }

    private void init(Context context) {
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

    /**
     * check if we have any stored user token
     * if true for us it means that we logged-in
     * otherwise it means that this is a first launch
     * or rider was logout
     *
     * @return Returns true if we have any stored user token
     */
    public boolean isLogged() {
        return getToken() != null;
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

    public Country getCountry() {
        String json = sharedPreferences.getString(Constants.Preferences.COUNTRY, "");

        return gson.fromJson(json, Country.class);
    }

    public boolean storeLanguage(Language language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(language);
        editor.putString(Constants.Preferences.LANGUAGE, json);

        return editor.commit();
    }

    public Language getLanguage() {
        String json = sharedPreferences.getString(Constants.Preferences.LANGUAGE, "");

        return gson.fromJson(json, Language.class);
    }

    /**
     * Clean all stored data
     */
    public void cleanSession() {
        storeStopList(Collections.emptyList());
        storeRiderLocation(null);
        storeToken(null);
        storeRider(null);
    }


    public void storeStopList(List<Stop> stopList) {
        this.stopList = getUpToDateList(stopList);
        cleanStatusMap();
    }

    public List<Stop> getStopList() {
        return stopList == null ? new LinkedList<>() : stopList;
    }

    /**
     * get delivery part of each route stop
     * no matter if it's pick-up or delivery type the arrival time would be
     * for delivery part of current order
     *
     * @param currentStop by order code of this stop we would search for delivery part
     * @return delivery part of any order
     */
    public Stop getDeliveryPartOfEachRouteStop(@NonNull Stop currentStop) {
        if (currentStop.getTask() == RouteStopTask.DELIVER) {
            return currentStop;
        }

        for (Stop stop : getStopList()) {
            if (currentStop.getOrderCode().equalsIgnoreCase(stop.getOrderCode())
                && stop.getTask() == RouteStopTask.DELIVER) {
                return stop;
            }
        }

        return null;
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

    public boolean hasNextStop() {
        return stopList != null && stopList.size() > 1;
    }

    public Stop getNextStop() {
        if (hasNextStop()) {
            return stopList.get(1);
        } else {
            return null;
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

    /**
     * When app is launching we retrieve rider information
     * and we need this information for rider details in sliding menu
     * and for issue reporting
     *
     * @return current rider information
     */
    @Nullable
    public Rider getRider() {
        return rider;
    }

    /**
     * Store rider information.
     *
     * @param rider current rider
     */
    void storeRider(Rider rider) {
        this.rider = rider;
    }

    /**
     * get rider location to be send with rider action
     * BI need this data for details about rider's actions
     *
     * @return location wrapped with additional info
     */
    @Nullable
    RiderLocation getRiderLocation() {
        return riderLocation;
    }

    /**
     * Every time when rider has new location we add more information with
     * accuracy battery level and store.
     * This data would be send with next rider action notifier
     *
     * @param riderLocation location wrapped with additional info
     */
    public void storeRiderLocation(@Nullable RiderLocation riderLocation) {
        this.riderLocation = riderLocation;
    }

    private List<Stop> getUpToDateList(List<Stop> stopList) {
        for (Stop stop : stopList) {
            if (stopStatusMap.get(stop.getId()) != null) {
                stop.setStatus(stopStatusMap.get(stop.getId()));
            }
        }

        //There should be any neither route stop with invalid order codes nor deprecated
        return removeInvalidOrderCodeStops(removeCompletedStops(stopList));
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

    /**
     * In case when server can have not up to date information
     * For instance when rider finished some order offline
     * and server hasn't received this data jet we need to exclude this orders from list
     *
     * @param stopList row data from server side, can contains already offline finished orders
     * @return filtered data without depricated orders
     */
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

    /**
     * Check if order code is valid for all route stop
     * And remove it from list if invalid.
     * </p>
     * Each route stop order code should fit requirements
     * 1) not empty
     * 2) length() == 9 including hyphen (this check is inside regexp)
     * 3) should follow pattern xxxx-yyyy, where xxxx - vendor code
     * and yyyy - customer code
     * <p>
     *
     * @param stopList list of up to date stop
     * @return list of up to date stops without stops with invalid order code
     */
    private List<Stop> removeInvalidOrderCodeStops(List<Stop> stopList) {
        for (Iterator<Stop> iterator = stopList.iterator(); iterator.hasNext(); ) {
            Stop stop = iterator.next();
            //check if order code fit our requirements
            if (TextUtils.isEmpty(stop.getOrderCode()) ||
                !Pattern.matches("^[a-zA-Z\\d]{4}-[a-zA-Z\\d]{4}$", stop.getOrderCode())) {
                iterator.remove();
            }
        }

        return stopList;
    }
}
