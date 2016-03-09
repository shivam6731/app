package com.foodpanda.urbanninja.utils;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.Country;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
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
    public void testGetValueWithCurrencySymbolWithNumeric() throws Exception {
        assertEquals("HKD2.00", FormatUtil.getValueWithCurrencySymbol(getCountryForCode("hk"), 2.0d));
    }

    @Test
    public void testGetValueWithCurrencySymbolWithUnknownCountry() throws Exception {
        assertEquals("", FormatUtil.getValueWithCurrencySymbol(new Country(), 2.0d));
    }

    private Country getCountryForCode(String code) {
        Country country = new Country();
        country.setCode(code);

        return country;
    }
}
