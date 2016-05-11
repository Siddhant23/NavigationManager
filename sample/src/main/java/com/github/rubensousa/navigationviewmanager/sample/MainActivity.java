package com.github.rubensousa.navigationviewmanager.sample;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.rubensousa.navigationviewmanager.NavigationViewManager;

public class MainActivity extends AppCompatActivity {

    private NavigationViewManager mNavigationViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationViewManager = new NavigationViewManagerImpl(getSupportFragmentManager(),
                navigationView, drawer);

        mNavigationViewManager.init(savedInstanceState, getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigationViewManager.saveNavigationState(outState);
    }

    @Override
    protected void onDestroy() {
        mNavigationViewManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mNavigationViewManager.closeDrawer()) {
            super.onBackPressed();
        }
    }

    public void setupToolbar(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        toolbar.setTitle(mNavigationViewManager.getCurrentTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationViewManager.openDrawer();
            }
        });
    }
}
