package com.foodpanda.urbanninja.manager;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.Language;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class LanguageManagerTest {
    @Mock
    private StorageManager storageManager;
    private Application app;
    private LanguageManager languageManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        app = RuntimeEnvironment.application;
        app.onCreate();

        when(storageManager.getLanguage()).thenReturn(new Language("hk", "hk"));

        languageManager = new LanguageManager(storageManager);
    }

    @Test
    public void setSetLanguage() {
        languageManager.setLanguage(app);
        assertEquals(app.getResources().getConfiguration().locale, new Locale(storageManager.getLanguage().getCode()));
    }
}
