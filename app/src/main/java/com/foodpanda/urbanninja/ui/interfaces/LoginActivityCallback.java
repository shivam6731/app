package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.model.Country;

public interface LoginActivityCallback {
    void onLoginSuccess(String username, String password);

    void onSelectCountryClicked(CountrySelectedCallback countrySelectedCallback);

    void onCountrySelected(Country country);
}
