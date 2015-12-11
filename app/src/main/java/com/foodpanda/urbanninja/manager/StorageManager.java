package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.TokenData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class StorageManager implements Managable {
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private Token token;

    @Override
    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        gson = new GsonBuilder().
            create();
    }

    public void storeToken(Token token) {
        this.token = token;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(token);
        editor.putString(Constants.Preferences.TOKEN, json);
        editor.commit();
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

            return null;
        }

    }
}
