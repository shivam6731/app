package com.foodpanda.urbanninja.ui.util;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.enums.DialogType;
import com.foodpanda.urbanninja.model.enums.PolygonStatusType;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class DialogInfoHelperTest {

    @Test
    public void showInformationDialogNoDataTest() throws Exception {
        Application app = RuntimeEnvironment.application;
        NestedFragmentCallback nestedFragmentCallback = mock(NestedFragmentCallback.class);

        DialogInfoHelper.showInformationDialog(app,
            PolygonStatusType.NO_DATA,
            "DeliveryZoneName",
            nestedFragmentCallback);

        verify(nestedFragmentCallback).openInformationDialog(
            "Activate GPS",
            "It looks like your GPS settings are not properly setup.\n" +
                "\n" +
                "        Please click on the link below and make sure you allow Urban Ninja to locate you while your're working\n" +
                "\n" +
                "        Note: it's not possible to work without GPS anabled",
            "go to gps setting",
            DialogType.GPS);
    }

    @Test
    public void showInformationDialogInsideTest() throws Exception {
        Application app = RuntimeEnvironment.application;
        NestedFragmentCallback nestedFragmentCallback = mock(NestedFragmentCallback.class);

        DialogInfoHelper.showInformationDialog(app,
            PolygonStatusType.INSIDE,
            "DeliveryZoneName",
            nestedFragmentCallback);

        verify(nestedFragmentCallback).openInformationDialog(
            "You successfully Clocked in!",
            "You just successfully started you shift.You will now be able to receive new orders.",
            "OK, let's start to work",
            DialogType.INFORMATION);
    }

    @Test
    public void showInformationDialogOutsideTest() throws Exception {
        Application app = RuntimeEnvironment.application;
        NestedFragmentCallback nestedFragmentCallback = mock(NestedFragmentCallback.class);
        DialogInfoHelper.showInformationDialog(app,
            PolygonStatusType.OUTSIDE,
            "DeliveryZoneName",
            nestedFragmentCallback);

        //No way to check message because Formatter message is Spannable
        //and there is not implementation of equals for this class
        verify(nestedFragmentCallback).openInformationDialog(
            contains("Go to DeliveryZoneName"),
            any(CharSequence.class),
            eq("OK, I'll get closer"),
            eq(DialogType.INFORMATION));
    }

}
