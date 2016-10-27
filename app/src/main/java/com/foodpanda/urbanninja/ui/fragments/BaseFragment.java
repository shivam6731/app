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
        setupComponent();
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

    /**
     * Base method to add nested fragment
     *
     * @param viewContainerId id of container where fragment would be added
     * @param baseFragment    fragment that would be added
     */
    protected void addFragment(int viewContainerId, BaseFragment baseFragment) {
        fragmentManager.
            beginTransaction().
            add(viewContainerId,
                baseFragment).
            commitAllowingStateLoss();
    }

    /**
     * In classes where we need to inject something
     * this method should be @Override and an instance of fragment should be passed as a param to the class.
     * we need such approach to let dagger know what code should be generated with direct passing an instance as a param.
     * you can check {@link dagger.Component} or quick tutorial https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2
     */
    protected void setupComponent() {
    }
}
