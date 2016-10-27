package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.ui.adapter.CountryAdapter;
import com.foodpanda.urbanninja.ui.interfaces.LoginActivityCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class CountryListFragment extends BaseListFragment<CountryAdapter> implements BaseApiCallback<CountryListWrapper> {
    private Country selectedCountry;
    private LoginActivityCallback loginActivityCallback;
    @Inject
    ApiManager apiManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginActivityCallback = (LoginActivityCallback) context;
    }

    public static CountryListFragment newInstance(Country country) {
        CountryListFragment fragment = new CountryListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.COUNTRY, country);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedCountry = getArguments().getParcelable(Constants.BundleKeys.COUNTRY);
    }

    @Override
    protected void setupComponent() {
        super.setupComponent();
        App.get(getContext()).getMainComponent().inject(this);
    }

    @Override
    protected CountryAdapter provideListAdapter() {
        return new CountryAdapter(new ArrayList<>(), activity);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.select_country_language_fragment;
    }

    @Override
    protected CharSequence provideEmptyListDescription() {
        return getResources().getText(R.string.empty_list_country);
    }

    @Override
    public void onItemClick(View view, int position) {
        selectedCountry = adapter.getItem(position);
        adapter.setSelectedCountry(adapter.getItem(position));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_select).setOnClickListener(v -> {
            if (selectedCountry != null) {
                loginActivityCallback.onCountrySelected(selectedCountry);
            }
        });
        activity.showProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getResources().getString(R.string.country_select_title), true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiManager.getCountries(this);
        adapter.setSelectedCountry(selectedCountry);
    }

    @Override
    public void onSuccess(CountryListWrapper countryListWrapper) {
        adapter.addAll(sortCountries(countryListWrapper.getData()));
        adapter.notifyDataSetChanged();
        activity.hideProgress();
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
        activity.hideProgress();
    }

    private List<Country> sortCountries(List<Country> list) {
        Collections.sort(list);

        return list;
    }
}
