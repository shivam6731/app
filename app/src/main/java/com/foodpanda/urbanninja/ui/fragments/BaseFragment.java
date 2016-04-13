package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.ui.dialog.ProgressDialogFragment;

public abstract class BaseFragment extends Fragment {
    protected BaseActivity activity;
    private ProgressDialogFragment progressDialogFragment;
    private boolean isProgressShowed;
    protected FragmentManager fragmentManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getChildFragmentManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isProgressShowed) {
            showProgressDialog();
        }
    }

    @Override
    public void onPause() {
        isProgressShowed = progressDialogFragment != null && progressDialogFragment.isVisible();
        hideProgressDialog();
        super.onPause();
    }

    protected void showProgressDialog() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(ft, ProgressDialogFragment.class.getSimpleName());
    }

    protected void hideProgressDialog() {
        if (progressDialogFragment != null &&
            progressDialogFragment.isAdded() &&
            !progressDialogFragment.isRemoving()) {
            progressDialogFragment.dismiss();
        }
        isProgressShowed = false;
    }
}
