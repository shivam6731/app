package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.foodpanda.urbanninja.App;
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
import java.util.Comparator;
import java.util.List;

public class CountryListFragment extends BaseListFragment<CountryAdapter> implements BaseApiCallback<CountryListWrapper> {
    private ApiManager apiManager;
    private Country selectedCountry;
    private LoginActivityCallback loginActivityCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginActivityCallback = (LoginActivityCallback) context;
    }

    public static CountryListFragment newInstance(Country country) {
        CountryListFragment fragment = new CountryListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Country.class.getSimpleName(), country);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
        selectedCountry = getArguments().getParcelable(Country.class.getSimpleName());
    }

    @Override
    protected CountryAdapter provideListAdapter() {

        return new CountryAdapter(new ArrayList(), activity);
    }

    @Override
    protected int provideListLayout() {

        return R.layout.select_country_fragment;
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
        view.findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getSupportFragmentManager().popBackStack();
            }
        });
        view.findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivityCallback.onCountrySelected(selectedCountry);
            }
        });
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
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }

    private List<Country> sortCountries(List<Country> list) {
        Collections.sort(list, new Comparator<Country>() {
            @Override
            public int compare(Country lhs, Country rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        return list;
    }
}
