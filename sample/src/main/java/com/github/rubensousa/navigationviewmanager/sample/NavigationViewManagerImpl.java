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

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import com.github.rubensousa.navigationviewmanager.NavigationViewManager;

public class NavigationViewManagerImpl extends NavigationViewManager {

    public NavigationViewManagerImpl(FragmentManager fragmentManager, NavigationView navigationView,
                                     DrawerLayout drawerLayout, @IdRes int containerId) {
        super(fragmentManager, navigationView, drawerLayout, containerId);
    }

    @Override
    public void showDefaultItem(NavigationView navigationView) {
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_import));
    }

    @NonNull
    @Override
    public Fragment createFragment(@IdRes int item) {
        Fragment fragment;

        switch (item) {
            // Add your other cases here
            default:
                fragment = new DummyFragment();
        }

        // Set some custom arguments
        Intent args = new Intent();
        args.putExtra(DummyFragment.TITLE, getCurrentTitle());
        fragment.setArguments(args.getExtras());
        return fragment;
    }

    /* Override this to create your custom fragment transactions
    @Override
    public FragmentTransaction createFragmentTransaction(Fragment fragment) {
        return mFragmentManager.beginTransaction()
                .replace(R.id.frameLayout,fragment,CURRENT_TITLE)
                .addToBackStack(null);
    }*/

}
