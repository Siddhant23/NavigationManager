package com.github.rubensousa.navigationviewmanager.sample;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.github.rubensousa.navigationviewmanager.NavigationViewManager;

public class NavigationViewManagerImpl extends NavigationViewManager {

    private FragmentManager mFragmentManager;

    public NavigationViewManagerImpl(FragmentManager fragmentManager, NavigationView navigationView,
                                     DrawerLayout drawerLayout) {
        super(fragmentManager, navigationView, drawerLayout);
        mFragmentManager = fragmentManager;
    }

    @Override
    public void showDefaultItem(NavigationView navigationView) {
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_camera));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (super.onNavigationItemSelected(item)) {
            Fragment currentFragment = DummyFragment.newInstance(item.getTitle().toString());

            if (currentFragment != null) {
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, currentFragment, CURRENT_TITLE)
                        .commit();

                setCurrentFragment(currentFragment);
            }

            return true;
        }
        return false;
    }
}
