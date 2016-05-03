package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.OrderReport;
import com.foodpanda.urbanninja.model.OrderStop;
import com.foodpanda.urbanninja.model.WorkingDay;
import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.model.enums.RouteStopStatus;
import com.foodpanda.urbanninja.ui.adapter.CashReportAdapter;
import com.foodpanda.urbanninja.ui.util.DividerItemDecoration;
import com.foodpanda.urbanninja.ui.widget.RecyclerViewEmpty;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class CashReportListFragment extends BaseFragment {
    private RecyclerViewEmpty recyclerView;

    public static CashReportListFragment newInstance() {
        CashReportListFragment loginFragment = new CashReportListFragment();

        return loginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);

        recyclerView = (RecyclerViewEmpty) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        TextView textViewEmpty = (TextView) view.findViewById(R.id.empty_view);
        textViewEmpty.setText(getResources().getText(R.string.empty_list_cash_report));
        recyclerView.setEmptyView(textViewEmpty);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CashReportAdapter adapter = new CashReportAdapter(list(), activity);
        recyclerView.setAdapter(adapter);
    }

    //TODO should be removed
    //This is test date it would be deleted with real API
    private List<WorkingDay> list() {
        List<WorkingDay> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            WorkingDay workingDay = new WorkingDay();
            workingDay.setDateTime(DateTime.now());
            workingDay.setTotal(i);
            List<OrderReport> orderReports = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                OrderReport orderReport = new OrderReport("code" + i, generateReportSteps());
                orderReports.add(orderReport);
            }
            workingDay.setOrderReports(orderReports);
            list.add(workingDay);
        }

        return list;
    }

    //TODO should be removed
    //This is test date it would be deleted with real API
    private List<OrderStop> generateReportSteps() {
        List<OrderStop> orderStops = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            orderStops.add(new OrderStop(
                "name" + i,
                i,
                i % 2 == 0 ? RouteStopStatus.DELIVER : RouteStopStatus.PICKUP,
                i % 3 == 0 ? Action.CANCELED : Action.COMPLETED));
        }

        return orderStops;
    }
}
