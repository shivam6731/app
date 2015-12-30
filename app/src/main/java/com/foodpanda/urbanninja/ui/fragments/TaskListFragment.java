package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.view.View;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.adapter.StopAdapter;

import java.util.LinkedList;
import java.util.List;

public class TaskListFragment extends BaseListFragment<StopAdapter> {
    private List<Stop> list = new LinkedList<>();

    public static TaskListFragment newInstance() {
        TaskListFragment fragment = new TaskListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 5; i++) {
            list.add(new Stop());
        }
    }

    @Override
    protected StopAdapter provideListAdapter() {
        return new StopAdapter(list, activity);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.base_list_fragment;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
