package com.foodpanda.urbanninja.utils;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.Country;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class FormatUtilTest {

    @Test
    public void testGetValueWithCurrencySymbol() throws Exception {
        assertEquals("HKD2.00", FormatUtil.getValueWithCurrencySymbol(getCountryForCode("hk"), "2.0"));
        assertEquals("HKD0.00", FormatUtil.getValueWithCurrencySymbol(getCountryForCode("hk"), "0"));
    }

    @Test
    public void testGetValueWithCurrencySymbolFromBadString() throws Exception {
        assertEquals("", FormatUtil.getValueWithCurrencySymbol(getCountryForCode("hk"), ""));
        assertEquals("", FormatUtil.getValueWithCurrencySymbol(getCountryForCode("hk"), "foobar"));
    }

    @Test
    public void testGetValueWithCurrencySymbolFromNullString() throws Exception {
        assertEquals("", FormatUtil.getValueWithCurrencySymbol(getCountryForCode("hk"), null));
    }

    @Test
    public void testGetValueWithCurrencySymbolWithNumeric() throws Exception {
        assertEquals("HKD2.00", FormatUtil.getValueWithCurrencySymbolFromNumber(getCountryForCode("hk"), 2.0d));
    }

    @Test
    public void testGetValueWithCurrencySymbolWithUnknownCountry() throws Exception {
        assertEquals("", FormatUtil.getValueWithCurrencySymbolFromNumber(new Country(), 2.0));
    }

    private Country getCountryForCode(String code) {
        Country country = new Country();
        country.setCode(code);

        return country;
    }

    @Test
    public void getPreOrderValueNullValue() throws Exception {
        Application application = RuntimeEnvironment.application;
        assertEquals("Preorder", FormatUtil.getPreOrderValue(null, application));
    }

    @Test
    public void getPreOrderValueWrongFormatValue() throws Exception {
        Application application = RuntimeEnvironment.application;
        assertEquals("Preorder", FormatUtil.getPreOrderValue("12qwe3123", application));
    }

    @Test
    public void getPreOrderValueWrongNormalValue() throws Exception {
        Application application = RuntimeEnvironment.application;
        assertEquals("Preorder for 15:53",
            FormatUtil.getPreOrderValue(
                DateTime.now().
                    withTime(0, 0, 0, 0).
                    plusHours(15).
                    plusMinutes(53).
                    toString(),
                application));
    }

    @Test
    public void testCurrencySymbolWithNumeric() throws Exception {
        assertEquals("HKD", FormatUtil.getCurrencySymbol(getCountryForCode("hk")));
    }

}
