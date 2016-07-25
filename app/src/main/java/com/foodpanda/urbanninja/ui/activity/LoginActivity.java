package com.foodpanda.urbanninja.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.LanguageManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.Language;
import com.foodpanda.urbanninja.ui.fragments.CountryListFragment;
import com.foodpanda.urbanninja.ui.fragments.LanguageListFragment;
import com.foodpanda.urbanninja.ui.fragments.LoginFragment;
import com.foodpanda.urbanninja.ui.interfaces.LoginActivityCallback;

public class LoginActivity extends BaseActivity implements LoginActivityCallback {
    private StorageManager storageManager;
    private ApiManager apiManager;

    private Country country;

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        storageManager = App.STORAGE_MANAGER;
        apiManager = App.API_MANAGER;
        if (isLogged()) {
            openMainActivity();

            return;
        }
        this.country = storageManager.getCountry();
        if (savedInstanceState == null) {
            loginFragment = LoginFragment.newInstance();
            fragmentManager.
                beginTransaction().
                add(R.id.container, loginFragment).
                commit();
        }
        initToolbar();
        changeActivityTitleOnBackPress();
    }

    /**
     * When user press back button in the device or at the action bar
     * title should be changed according to the next fragment in the stack
     */
    private void changeActivityTitleOnBackPress() {
        fragmentManager.addOnBackStackChangedListener(() -> {
            Fragment fragment = fragmentManager.findFragmentById(R.id.container);
            if (fragment instanceof LoginFragment) {
                setTitle(getResources().getString(R.string.logic_title), false);
            }
            if (fragment instanceof CountryListFragment) {
                setTitle(getResources().getString(R.string.country_select_title), true);
            }
            if (fragment instanceof LanguageListFragment) {
                setTitle(getResources().getString(R.string.language_select_title), true);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // we have a home button in the action bar with the same behaviour as
        // back button so that's why we call onBackPressed method
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onLoginSuccess(String username, String password) {
        storageManager.storeUserName(username);
        storageManager.storePassword(password);
        openMainActivity();
    }

    @Override
    public void onSelectCountryClicked() {
        fragmentManager.
            beginTransaction().
            add(R.id.container, CountryListFragment.newInstance(country)).
            addToBackStack(CountryListFragment.class.getSimpleName()).
            commit();
        hideKeyboard();
    }

    @Override
    public void onCountrySelected(Country country) {
        this.country = country;
        storageManager.storeCountry(country);
        apiManager.init(this);

        fragmentManager.
            beginTransaction().
            add(R.id.container, LanguageListFragment.newInstance(storageManager.getLanguage())).
            addToBackStack(LanguageListFragment.class.getSimpleName()).
            commit();
    }

    /**
     * Update Ui for LoginFragment according to selected language
     *
     * @param language selected language
     */
    @Override
    public void onLanguageSelected(Language language) {
        // clean back stack
        // we need to do it two times because in stack we have two fragments
        // CountryListFragment and LanguageListFragment
        fragmentManager.popBackStack();
        fragmentManager.popBackStack();

        // store selected language
        storageManager.storeLanguage(language);

        // update all resources according to selected language
        new LanguageManager(storageManager).setLanguage(this);

        // update UI for LoginFragment
        getSupportFragmentManager()
            .beginTransaction()
            .detach(loginFragment)
            .attach(loginFragment)
            .commit();
        // update selected country textView in LoginFragment
        // and put it in selected language
        if (loginFragment != null) {
            loginFragment.setCountryNameWithLanguage(country);
        }
    }

    private boolean isLogged() {
        return storageManager.getToken() != null;
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }
    }
}

