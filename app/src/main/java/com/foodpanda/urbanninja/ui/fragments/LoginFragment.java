package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.ui.interfaces.LoginActivityCallback;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class LoginFragment extends BaseFragment implements Validator.ValidationListener, BaseApiCallback<Token> {
    private ApiManager apiManager;

    @NotEmpty
    private EditText editEmail;
    @Password(min = 6, scheme = Password.Scheme.ALPHA)
    private EditText editPassword;

    private Validator validator;

    private LoginActivityCallback loginActivityCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginActivityCallback = (LoginActivityCallback) context;
    }

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        
        return loginFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
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

        Button mEmailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        apiManager.login(editEmail.getText().toString(),
            editPassword.getText().toString(), this);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(activity);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }
    }

    @Override
    public void onSuccess(Token token) {
        loginActivityCallback.onLoginSuccess();
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }
}
