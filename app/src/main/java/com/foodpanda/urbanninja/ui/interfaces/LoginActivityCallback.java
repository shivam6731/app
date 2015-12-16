package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.model.Country;

public interface LoginActivityCallback {
    void onLoginSuccess();

    void onSelectCountryClicked();

    void onCountrySelected(Country country);
}
