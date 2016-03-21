package com.foodpanda.urbanninja;

import android.app.Application;

import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class StorageManagerTest {
    private StorageManager storageManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        storageManager = new StorageManager();
        storageManager.init(app);
    }

    @Test
    public void testStoreToken() {
        Token token = new Token("", "", 0, "");
        assertTrue(storageManager.storeToken(token));
        assertEquals(storageManager.getToken(), token);
    }

    @Test
    public void testClearToken() {
        Token token = new Token("", "", 0, "");
        assertTrue(storageManager.storeToken(token));
        storageManager.cleanSession();
        assertEquals(storageManager.getToken(), null);
        assertTrue(storageManager.getStopList().isEmpty());
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
