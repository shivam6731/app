package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Country;

import java.util.List;
import java.util.Locale;

public class CountryAdapter extends SimpleBaseAdapter<Country, CountryAdapter.ViewHolder> {
    private Country selectedCountry;

    public CountryAdapter(List<Country> objects, Context context) {
        super(objects, context);
    }

    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Country country = getItem(position);
        if (!TextUtils.isEmpty(country.getCode())) {
            String title = new Locale("", country.getCode()).getDisplayCountry();
            holder.txtName.setText(title);
        }
        if (selectedCountry != null &&
            selectedCountry.getCode().equalsIgnoreCase(getItem(position).getCode())) {
            holder.radioBtnSelectedCountry.setChecked(true);
        } else {
            holder.radioBtnSelectedCountry.setChecked(false);
        }

    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public RadioButton radioBtnSelectedCountry;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_country_name);
            radioBtnSelectedCountry = (RadioButton) itemView.findViewById(R.id.radio_btn_selected_country);
        }
    }

    public void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;
    }
}
