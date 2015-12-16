package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.ui.adapter.SimpleBaseAdapter;
import com.foodpanda.urbanninja.ui.util.DividerItemDecoration;
import com.foodpanda.urbanninja.ui.widget.RecyclerViewEmpty;

public abstract class  BaseListFragment<U extends SimpleBaseAdapter> extends BaseFragment implements SimpleBaseAdapter
    .OnItemClickListener {
    private RecyclerViewEmpty recyclerView;
    private View emptyView;

    protected U adapter;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        int layoutId = provideListLayout();

        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerViewEmpty) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(provideLayoutManager());

        emptyView = view.findViewById(R.id.empty_view);
        recyclerView.setEmptyView(emptyView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = provideListAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    protected abstract U provideListAdapter();

    protected abstract int provideListLayout();

    protected RecyclerView.LayoutManager provideLayoutManager() {

        return new LinearLayoutManager(activity);
    }

}
