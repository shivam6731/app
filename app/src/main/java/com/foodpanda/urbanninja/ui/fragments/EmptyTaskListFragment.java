package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodpanda.urbanninja.R;

public class EmptyTaskListFragment extends BaseFragment {

    public static EmptyTaskListFragment newInstance() {
        EmptyTaskListFragment emptyTaskListFragment = new EmptyTaskListFragment();

        return emptyTaskListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.empty_task_list_fragment, container, false);
    }


}
