package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LanguageManager {
    private StorageManager storageManager;

    public LanguageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Force to set selected locale to the whole app
     * it will replace not only string resources,
     * but also all internal android name such as date formatter names
     * and default name for dialog buttons
     * (for instance 'OK' and 'Cancel' for dialogs)
     *
     * @param context to get access to Configuration
     */
    public void setLanguage(Context context) {
        Locale locale = getLanguageLocale();
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        setLocaleToConfig(locale, config);

        context.getResources().updateConfiguration(
            config,
            context.getResources().getDisplayMetrics()
        );
    }

    /**
     * get selected language from storage manager
     * and create Locale with language code
     *
     * @return locale from selected language
     */
    private Locale getLanguageLocale() {
        return storageManager.getLanguage() != null ?
            new Locale(storageManager.getLanguage().getCode()) :
            Locale.getDefault();
    }

    /**
     * this setter is new feature from android 17 however our min support version is 15
     * that's why we need to check both deprecated and new version of this param
     *
     * @param locale locale that should be set
     * @param config config to set locale
     * @return config with locale
     */
    private Configuration setLocaleToConfig(Locale locale, Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        return config;
    }
}
