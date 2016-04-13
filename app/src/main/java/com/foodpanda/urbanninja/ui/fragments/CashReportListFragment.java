package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.CashReport;
import com.foodpanda.urbanninja.ui.adapter.CashReportAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;

public class CashReportListFragment extends BaseListFragment<CashReportAdapter> {

    public static CashReportListFragment newInstance() {
        CashReportListFragment loginFragment = new CashReportListFragment();

        return loginFragment;
    }

    @Override
    protected CashReportAdapter provideListAdapter() {

        return new CashReportAdapter(list(), activity);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.base_list_fragment;
    }

    @Override
    protected CharSequence provideEmptyListDescription() {
        return getResources().getText(R.string.empty_list_cash_report);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.removeItemDecoration(dividerItemDecoration);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //TODO should be removed
    //This is test date it would be deleted with real API
    private List<CashReport> list() {
        List<CashReport> list = new LinkedList<>();
        for (int i = 0; i < 45; i++) {
            DateTime dateTime = i > 14 ? DateTime.now().plusDays(2) : DateTime.now();
            if (i > 25) {
                dateTime = DateTime.now().plusDays(3);
            }
            if (i > 35) {
                dateTime = DateTime.now().plusDays(5);
            }
            list.add(new CashReport(
                "code" + i,
                "name" + i + " name" + i + " name" + i,
                i,
                dateTime,
                i % 7 == 0,
                i % 2 == 0));
        }

        return list;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
