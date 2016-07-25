package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Language;
import com.foodpanda.urbanninja.ui.adapter.LanguageAdapter;
import com.foodpanda.urbanninja.ui.interfaces.LoginActivityCallback;

import java.util.ArrayList;
import java.util.List;

public class LanguageListFragment extends BaseListFragment<LanguageAdapter> {
    private Language selectedLanguage;
    private LoginActivityCallback loginActivityCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginActivityCallback = (LoginActivityCallback) context;
    }

    public static LanguageListFragment newInstance(Language language) {
        LanguageListFragment fragment = new LanguageListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.LANGUAGE, language);
        fragment.setArguments(bundle);
        
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedLanguage = getArguments().getParcelable(Constants.BundleKeys.LANGUAGE);
    }

    @Override
    protected LanguageAdapter provideListAdapter() {
        return new LanguageAdapter(createLanguageList(), activity);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.select_country_language_fragment;
    }

    @Override
    protected CharSequence provideEmptyListDescription() {
        return getResources().getText(R.string.base_empty_list);
    }

    @Override
    public void onItemClick(View view, int position) {
        selectedLanguage = adapter.getItem(position);
        adapter.setSelectedLanguage(adapter.getItem(position));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_select).setOnClickListener(v -> {
            if (selectedLanguage != null) {
                loginActivityCallback.onLanguageSelected(selectedLanguage);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getResources().getString(R.string.language_select_title), true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter.setSelectedLanguage(selectedLanguage);
    }

    /**
     * retrieve list of language names and codes from resources
     * to show them in a list
     *
     * @return list of supported countries
     */
    private List<Language> createLanguageList() {
        List<Language> languages = new ArrayList<>();

        String[] languageCodes = getResources().getStringArray(R.array.language_codes);
        String[] languageNames = getResources().getStringArray(R.array.language_names);

        for (int i = 0; i < languageCodes.length; i++) {
            languages.add(new Language(languageNames[i], languageCodes[i]));
        }

        return languages;
    }
}

