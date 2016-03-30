package com.foodpanda.urbanninja;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class BaseApplicationTest {

    @Test
    public void shouldInitializeCrashReportingFromBaseComponent() {
        // given
        App testBaseApplication = (App) RuntimeEnvironment.application;
        testBaseApplication.onCreate();
        // then

        assertNotNull(testBaseApplication);
        assertNotNull(App.API_MANAGER);
        assertNotNull(App.STORAGE_MANAGER);
    }
}
