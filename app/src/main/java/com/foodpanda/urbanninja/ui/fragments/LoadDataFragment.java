package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodpanda.urbanninja.R;

public class LoadDataFragment extends BaseFragment {
    public static LoadDataFragment newIntance() {
        LoadDataFragment loadDataFragment = new LoadDataFragment();

        return loadDataFragment;
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.load_data_fragment, container, false);
    }

}
