package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Language;

import java.util.List;

public class LanguageAdapter extends SimpleBaseAdapter<Language, LanguageAdapter.ViewHolder> {
    private Language selectedLanguage;

    public LanguageAdapter(List<Language> objects, Context context) {
        super(objects, context);
    }

    @Override
    public LanguageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country_language, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Language language = getItem(position);
        if (!TextUtils.isEmpty(language.getName())) {
            holder.txtName.setText(language.getName());
        }
        if (selectedLanguage != null &&
            selectedLanguage.getCode().equalsIgnoreCase(getItem(position).getCode())) {
            holder.radioBtnSelectedLanguage.setChecked(true);
        } else {
            holder.radioBtnSelectedLanguage.setChecked(false);
        }

    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public RadioButton radioBtnSelectedLanguage;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_country_name);
            radioBtnSelectedLanguage = (RadioButton) itemView.findViewById(R.id.radio_btn_selected_country);
        }
    }

    public void setSelectedLanguage(Language selectedCountry) {
        this.selectedLanguage = selectedCountry;
    }

}

