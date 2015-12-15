package com.foodpanda.urbanninja.ui.fragments;

import android.view.View;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.ui.adapter.CountryAdapter;

import java.util.ArrayList;

public class CountryListFragment extends BaseListFragment<CountryAdapter> {
    private Country selectedCountry;

    public static CountryListFragment newInstance() {
        CountryListFragment fragment = new CountryListFragment();

        return fragment;
    }

    @Override
    protected CountryAdapter provideListAdapter() {

        return new CountryAdapter(new ArrayList(), activity);
    }

    @Override
    protected int provideListLayout() {
        
        return R.layout.base_list_fragment;
    }

    @Override
    public void onItemClick(View view, int position) {
        selectedCountry = adapter.getItem(position);
    }
}
