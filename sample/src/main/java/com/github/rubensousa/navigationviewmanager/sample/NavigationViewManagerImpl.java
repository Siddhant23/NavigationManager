/*
 * Copyright 2016 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
