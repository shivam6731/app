package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.ui.dialog.ProgressDialogFragment;

public abstract class BaseFragment extends Fragment {
    protected BaseActivity activity;
    private ProgressDialogFragment progressDialogFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
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
    }
}
