package com.foodpanda.urbanninja;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.foodpanda.urbanninja.api.serializer.DateTimeDeserializer;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class StorageManagerTest {

    private static final String APP_NAME = "APP_NAME";

    private final Gson gson = new GsonBuilder().
        registerTypeAdapter(DateTime.class, new DateTimeDeserializer()).
        create();

    @Mock
    Context context;

    @Mock
    Resources resources;

    @Mock
    SharedPreferences sharedPreferences;

    @Mock
    SharedPreferences cachedRequestPreferences;

    @Mock
    SharedPreferences.Editor editor;

    private StorageManager storageManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(resources.getString(R.string.app_name)).thenReturn(APP_NAME);

        when(context.getResources()).thenReturn(resources);

        when(context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences);

        when(context.getSharedPreferences(Constants.Preferences.CACHED_REQUESTS_PREFERENCES_NAME, Context.MODE_PRIVATE))
            .thenReturn(cachedRequestPreferences);

        when(sharedPreferences.edit()).thenReturn(editor);

        storageManager = new StorageManager();
        storageManager.init(context);
    }

    @Test
    public void testStoreToken() {
        when(editor.commit()).thenReturn(true);

        Token token = new Token("", "", 0, "");
        assertTrue(storageManager.storeToken(token));

        String json = gson.toJson(token);
        verify(editor).putString(Constants.Preferences.TOKEN, json);
    }

    @Test
    public void testGetCurrentStopEmpty() {
        storageManager.storeStopList(new LinkedList<Stop>());

        assertNull(storageManager.getCurrentStop());
    }

    @Test
    public void testGetCurrentStopFirst() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stop = new Stop();
        stops.add(stop);
        stops.add(new Stop());

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getCurrentStop(), stop);
    }

    @Test
    public void testRemoveCurrentStopEmpty() {
        LinkedList<Stop> stops = new LinkedList<>();

        storageManager.storeStopList(stops);
        assertNull(storageManager.removeCurrentStop());
    }

    @Test
    public void testRemoveCurrentStopFirst() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stop = new Stop();
        stops.add(stop);
        stops.add(new Stop());

        storageManager.storeStopList(stops);
        assertEquals(storageManager.removeCurrentStop(), stop);
    }
}
