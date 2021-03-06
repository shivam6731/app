package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
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
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.ui.adapter.CashReportAdapter;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.util.DividerItemDecoration;
import com.foodpanda.urbanninja.ui.widget.RecyclerViewEmpty;

import java.util.Collections;

import javax.inject.Inject;

public class CashReportListFragment extends BaseFragment implements BaseApiCallback<OrdersReportCollection> {
    private MainActivityCallback mainActivityCallback;
    @Inject
    ApiManager apiManager;
    @Inject
    StorageManager storageManager;

    private RecyclerViewEmpty recyclerView;

    public static CashReportListFragment newInstance() {
        CashReportListFragment loginFragment = new CashReportListFragment();

        return loginFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @Override
    protected void setupComponent() {
        super.setupComponent();
        App.get(activity).getMainComponent().inject(this);
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
        recyclerView.setAdapter(new CashReportAdapter(Collections.emptyList(), activity, storageManager.getCountry()));
        activity.showProgress();
        apiManager.getWorkingDayReport(this);
        mainActivityCallback.writeFragmentTitle(getResources().getString(R.string.side_menu_cash_report));
    }

    @Override
    public void onSuccess(OrdersReportCollection workingDays) {
        CashReportAdapter adapter = new CashReportAdapter(workingDays, activity, storageManager.getCountry());
        recyclerView.setAdapter(adapter);
        activity.hideProgress();
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }
}
