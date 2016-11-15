package com.foodpanda.urbanninja.ui.widget;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.ui.adapter.CountryAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class RecyclerViewEmptyTest {
    private Application app;

    private TextView textViewDescription;
    private RecyclerViewEmpty recyclerViewEmpty;

    private CountryAdapter viewHolderAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        app = RuntimeEnvironment.application;
        app.onCreate();
        textViewDescription = new TextView(app);

        recyclerViewEmpty = new RecyclerViewEmpty(app);
        recyclerViewEmpty.setEmptyView(textViewDescription);
    }

    @Test
    public void testNullAdapter() {
        recyclerViewEmpty.setAdapter(null);
        assertTrue(recyclerViewEmpty.getVisibility() == View.VISIBLE);
        assertTrue(textViewDescription.getVisibility() == View.VISIBLE);
    }

    @Test
    public void testEmptyAdapter() {
        viewHolderAdapter = new CountryAdapter(new LinkedList<Country>(), app.getApplicationContext());
        recyclerViewEmpty.setAdapter(viewHolderAdapter);
        assertTrue(recyclerViewEmpty.getVisibility() == View.GONE);
        assertTrue(textViewDescription.getVisibility() == View.VISIBLE);
    }

    @Test
    public void testNotEmptyAdapter() {
        List<Country> countries = new LinkedList<>();
        countries.add(new Country());

        viewHolderAdapter = new CountryAdapter(countries, app.getApplicationContext());
        recyclerViewEmpty.setAdapter(viewHolderAdapter);
        assertTrue(recyclerViewEmpty.getVisibility() == View.VISIBLE);
        assertTrue(textViewDescription.getVisibility() == View.GONE);
    }
}
