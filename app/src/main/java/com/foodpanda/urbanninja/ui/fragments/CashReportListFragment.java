package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.OrdersReportCollection;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.ui.adapter.CashReportAdapter;
import com.foodpanda.urbanninja.ui.util.DividerItemDecoration;
import com.foodpanda.urbanninja.ui.widget.RecyclerViewEmpty;

public class CashReportListFragment extends BaseFragment implements BaseApiCallback<OrdersReportCollection> {
    private ApiManager apiManager;

    private RecyclerViewEmpty recyclerView;

    public static CashReportListFragment newInstance() {
        CashReportListFragment loginFragment = new CashReportListFragment();

        return loginFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
            getActivity(),
            DividerItemDecoration.VERTICAL_LIST,
            R.drawable.divider);

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
        activity.showProgress();
        apiManager.getWorkingDayReport(this);
    }

    @Override
    public void onSuccess(OrdersReportCollection workingDays) {
        CashReportAdapter adapter = new CashReportAdapter(workingDays, activity);
        recyclerView.setAdapter(adapter);
        activity.hideProgress();
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }
}
