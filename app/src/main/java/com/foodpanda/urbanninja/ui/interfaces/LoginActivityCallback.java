package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.Language;

public interface LoginActivityCallback {
    /**
     * Callback from Login Fragment that we sucessfully logged-in
     * and MainActivity should be opened.
     * Moreover we should store user's login and password
     *
     * @param username rider name
     * @param password rider password
     */
    void onLoginSuccess(String username, String password);

    /**
     * Callback to redirect to the select country fragment
     */
    void onSelectCountryClicked();

    /**
     * Callback to store selected country
     * and redirect to the language selection fragment
     *
     * @param country selected country that should be stored
     */
    void onCountrySelected(Country country);

    /**
     * Callback to store selected language,
     * update UI according to the selected language
     * and finish the login process
     *
     * @param language selected language that should be stored
     */
    void onLanguageSelected(Language language);
}
