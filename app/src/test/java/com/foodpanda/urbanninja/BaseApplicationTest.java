package com.foodpanda.urbanninja;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class BaseApplicationTest {

    @Test
    public void shouldInitializeCrashReportingFromBaseComponent() {
        // given
        App testBaseApplication = (App) RuntimeEnvironment.application;
        testBaseApplication.onCreate();
        // then

        assertNotNull(testBaseApplication);
        assertNotNull(App.get(testBaseApplication));
        assertNotNull(App.get(testBaseApplication).getMainComponent());
        assertFalse(App.get(testBaseApplication).isMainActivityVisible());
    }
}
