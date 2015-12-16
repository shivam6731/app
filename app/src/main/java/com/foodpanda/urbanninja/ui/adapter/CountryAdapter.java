package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Country;

import java.util.List;

public class CountryAdapter extends SimpleBaseAdapter<Country, CountryAdapter.ViewHolder> {
    private Country selectedCountry;

    public CountryAdapter(List objects, Context context) {
        super(objects, context);
    }

    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Country country = getItem(position);
        if (!TextUtils.isEmpty(country.getTitle())) {
            holder.txtName.setText(country.getTitle());
        }
        if (selectedCountry == getItem(position)) {
            holder.imageSelected.setVisibility(View.VISIBLE);
        } else {
            holder.imageSelected.setVisibility(View.INVISIBLE);
        }

    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public ImageView imageSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_country_name);
            imageSelected = (ImageView) itemView.findViewById(R.id.image_selected_country);
        }
    }

    public void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;
    }
}
