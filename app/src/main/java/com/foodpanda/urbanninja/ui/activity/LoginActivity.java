package com.foodpanda.urbanninja.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.ui.fragments.CountryListFragment;
import com.foodpanda.urbanninja.ui.fragments.LoginFragment;
import com.foodpanda.urbanninja.ui.interfaces.LoginActivityCallback;

public class LoginActivity extends BaseActivity implements LoginActivityCallback {
    private StorageManager storageManager;
    private Country country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_container);
        storageManager = App.STORAGE_MANAGER;
        if (checkIsLogged()) {
            openMainActivity();

            return;
        }
        hideActionBar();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().
                add(R.id.container, LoginFragment.newInstance()).
                commit();
        }
    }

    @Override
    public void onLoginSuccess() {
        openMainActivity();
    }

    @Override
    public void onSelectCountryClicked() {
        fragmentManager.beginTransaction().
            add(R.id.container, CountryListFragment.newInstance()).
            addToBackStack(CountryListFragment.class.getSimpleName()).
            commit();

    }

    @Override
    public void onCountrySelected(Country country) {
        fragmentManager.popBackStack();
        this.country = country;
    }

    private boolean checkIsLogged() {

        return storageManager.getToken() != null;
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

