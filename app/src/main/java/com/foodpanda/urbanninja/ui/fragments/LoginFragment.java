package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.ui.interfaces.CountrySelectedCallback;
import com.foodpanda.urbanninja.ui.interfaces.LoginActivityCallback;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;
import java.util.Locale;

public class LoginFragment extends BaseFragment implements
    Validator.ValidationListener,
    BaseApiCallback<Token>,
    CountrySelectedCallback {

    private ApiManager apiManager;
    private StorageManager storageManager;

    @NotEmpty
    private EditText editEmail;
    @Password(min = 6, scheme = Password.Scheme.ALPHA)
    private EditText editPassword;
    @NotEmpty
    private TextView txtCountry;

    private Validator validator;

    private LoginActivityCallback loginActivityCallback;

    private String username;
    private String password;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginActivityCallback = (LoginActivityCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginActivityCallback = null;
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
        storageManager = App.STORAGE_MANAGER;
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the login form.
        editEmail = (EditText) view.findViewById(R.id.edit_username);
        editPassword = (EditText) view.findViewById(R.id.edit_password);
        txtCountry = (TextView) view.findViewById(R.id.edit_country);
        view.findViewById(R.id.edit_country).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPassword.setError(null);
                editEmail.setError(null);
                loginActivityCallback.onSelectCountryClicked(LoginFragment.this);
            }
        });

        Button emailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getResources().getString(R.string.logic_title), false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editEmail.setText(storageManager.getUsername());
        editPassword.setText(storageManager.getPassword());
        if (storageManager.getCountry() != null) {
            String title = new Locale("", storageManager.getCountry().getCode()).getDisplayCountry();
            txtCountry.setText(title);
        }
    }

    private void attemptLogin() {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        if (storageManager.getCountry() == null) {
            Toast.makeText(activity, getResources().getString(R.string.login_country_error), Toast.LENGTH_SHORT).show();
        } else {
            showProgressDialog();
            username = editEmail.getText().toString();
            password = editPassword.getText().toString();
            apiManager.login(username, password, this);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(activity);

            if (view instanceof EditText) {

                if (view.getId() == R.id.edit_country) {
                    Toast.makeText(activity,
                        getResources().getString(R.string.error_field_required),
                        Toast.LENGTH_SHORT
                    ).show();
                } else {
                    ((EditText) view).setError(message);
                }
            }
        }
    }

    @Override
    public void onSuccess(Token token) {
        hideProgressDialog();
        if (loginActivityCallback != null)
            loginActivityCallback.onLoginSuccess(username, password);
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        hideProgressDialog();
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }

    @Override
    public void onCountrySelected(Country country) {
        String title = new Locale("", country.getCode()).getDisplayCountry();
        txtCountry.setText(title);
        activity.setTitle(getResources().getString(R.string.logic_title), false);
    }
}
