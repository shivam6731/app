package com.foodpanda.urbanninja.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.ui.fragments.MainContainerFragment;
import com.foodpanda.urbanninja.ui.fragments.SlideMenuFragment;

public class MainActivity extends BaseActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().
                add(R.id.container, MainContainerFragment.newInstance()).commit();

            fragmentManager.beginTransaction().
                add(R.id.left_drawer, SlideMenuFragment.newInstance()).commit();
        }
    }

}
