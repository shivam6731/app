package com.foodpanda.urbanninja.ui.adapter;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.CashReport;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class CashReportAdapterTest {
    private CashReportAdapter cashReportAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        cashReportAdapter = new CashReportAdapter(list(), app);
    }

    @Test
    public void testGetTotalOfTheDayEmpty() {
        assertEquals(0, cashReportAdapter.getTotalOfTheDay(0), 0);
    }

    @Test
    public void testGetTotalOfTheDayFirstDayStartShouldBeOneItemValue() {
        assertEquals(1, cashReportAdapter.getTotalOfTheDay(1), 0);
    }

    @Test
    public void testGetTotalOfTheDayFirstDayEndShouldBeSumOfItemsValues() {
        assertEquals(325, cashReportAdapter.getTotalOfTheDay(25), 0);
    }

    @Test
    public void testGetTotalOfTheDaySecondDayStartShouldBeOneItemValue() {
        assertEquals(26, cashReportAdapter.getTotalOfTheDay(26), 0);
    }

    @Test
    public void testGetTotalOfTheDaySecondDayEndShouldBeOneSumOfItemsValues() {
        assertEquals(270, cashReportAdapter.getTotalOfTheDay(34), 0);
    }

    @Test
    public void testGetTotalOfTheDayThirdDayMiddleShouldBeSumOfAllPrevItemsValues() {
        assertEquals(190, cashReportAdapter.getTotalOfTheDay(40), 0);
    }

    @Test
    public void testGetTotalOfTheDayThirdDayEndShouldBeSumOfItemsValues() {
        assertEquals(360, cashReportAdapter.getTotalOfTheDay(44), 0);
    }

    private List<CashReport> list() {
        List<CashReport> list = new LinkedList<>();
        for (int i = 0; i < 45; i++) {
            DateTime dateTime = DateTime.now();
            if (i > 25) {
                dateTime = DateTime.now().plusDays(3);
            }
            if (i > 35) {
                dateTime = DateTime.now().plusDays(5);
            }
            list.add(new CashReport(
                "code" + i,
                "name" + i,
                i,
                dateTime,
                false,
                false));
        }

        return list;
    }
}
