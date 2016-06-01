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

package com.github.rubensousa.navigationviewmanager;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class NavigationViewManager implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

    public static final String NAVIGATE_ID = "navigate_id";
    public static final String CURRENT_ID = "current_id";
    public static final String CURRENT_TITLE = "current_title";
    public static final String ACTION_MODE_SUSPENDED = "action_mode_state";
    public static final String ACTION_MODE_ACTIVE = "action_mode_active";

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private NavigationListener mNavigationListener;
    private ActionModeListener mActionModeListener;
    private FragmentManager mFragmentManager;
    private Intent mIntent;
    private Fragment mCurrentFragment;
    private int mContainerId;
    private int mCurrentId;
    private String mTitle;

    public NavigationViewManager(FragmentManager fragmentManager, NavigationView navigationView,
                                 DrawerLayout drawerLayout, @IdRes int containerId) {
        mFragmentManager = fragmentManager;
        mNavigationView = navigationView;
        mDrawerLayout = drawerLayout;
        mContainerId = containerId;
        mDrawerLayout.addDrawerListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    public void setupDrawerToggle(AppCompatActivity activity, @StringRes int openDrawerRes,
                                  @StringRes int closeDrawerRes) {

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout,
                null, openDrawerRes, closeDrawerRes);
        drawerToggle.syncState();
    }

    public void init(Bundle savedInstanceState, Intent intent) {
        if (savedInstanceState != null) {
            mCurrentId = savedInstanceState.getInt(CURRENT_ID);
            mTitle = savedInstanceState.getString(CURRENT_TITLE);
            mCurrentFragment = mFragmentManager.findFragmentByTag(CURRENT_TITLE);
        } else {
            mIntent = intent;
            if (mIntent == null) {
                showDefaultItem(mNavigationView);
            } else {
                Bundle args = mIntent.getExtras();
                if (args != null) {
                    int menuId = args.getInt(NAVIGATE_ID);
                    MenuItem item = mNavigationView.getMenu().findItem(menuId);
                    if (item != null) {
                        onNavigationItemSelected(item);
                    }
                } else {
                    showDefaultItem(mNavigationView);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_ID, mCurrentId);
        outState.putString(CURRENT_TITLE, mTitle);
    }

    public void onDestroy() {
        mDrawerLayout.removeDrawerListener(this);
        mNavigationView.setNavigationItemSelectedListener(null);
    }

    public abstract void showDefaultItem(NavigationView navigationView);

    @NonNull
    public abstract Fragment createFragment(@IdRes int item);

    /**
     * Navigate via Intent. The intent must contain a NAVIGATE_ID field with the menu id of the
     * section desired to navigate to.
     *
     * @param intent Intent to be passed to the new fragment
     */
    public void navigateWithIntent(Intent intent) {
        mIntent = intent;
        if (mIntent != null) {
            Bundle args = mIntent.getExtras();
            if (args != null) {
                int menuId = args.getInt(NAVIGATE_ID);
                navigate(menuId);
            }
        }
    }

    public void navigate(@IdRes int menuId) {
        if (mCurrentId != menuId) {
            MenuItem lastItem = mNavigationView.getMenu().findItem(mCurrentId);
            lastItem.setChecked(false);
            MenuItem newItem = mNavigationView.getMenu().findItem(menuId);
            if (newItem != null) {
                onNavigationItemSelected(newItem);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mNavigationListener != null) {
            mNavigationListener.onItemSelected(item);
        }

        closeDrawer();

        if (item.getItemId() == mCurrentId) {
            return false;
        }

        mCurrentId = item.getItemId();
        mTitle = item.getTitle().toString();

        // If an item is checkable, then a fragment should be used
        if (item.isCheckable()) {
            item.setChecked(true);
            mCurrentFragment = createFragment(item.getItemId());

            // Check if we have an Intent to pass to the fragment
            if (mIntent != null) {
                Bundle args = mCurrentFragment.getArguments();
                Bundle extras = mIntent.getExtras();

                // Add the fragment's arguments to the new Intent
                if (args != null && extras != null) {
                    args.putAll(extras);
                }
                mCurrentFragment.setArguments(args);
                mIntent = null;
            }

            createFragmentTransaction(mCurrentFragment).commit();
            return true;
        }

        return false;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

        if (slideOffset == 0) {
            onDrawerClosed(drawerView);
        } else {
            onDrawerOpened(drawerView);
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {

        if (mActionModeListener != null
                && mActionModeListener.isActionModeActive()) {
            mActionModeListener.onSuspendActionMode();
        }

        if (mCurrentFragment instanceof ActionModeListener) {
            ActionModeListener fragCallbacks
                    = ((ActionModeListener) mCurrentFragment);

            if (fragCallbacks.isActionModeActive()) {
                fragCallbacks.onSuspendActionMode();
            }
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {

        if (mActionModeListener != null
                && mActionModeListener.isActionModeSuspended()) {
            mActionModeListener.onResumeActionMode();
        }

        if (mCurrentFragment instanceof ActionModeListener) {
            ActionModeListener fragCallbacks
                    = ((ActionModeListener) mCurrentFragment);

            if (fragCallbacks.isActionModeSuspended()) {
                fragCallbacks.onResumeActionMode();
            }
        }

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public int getCurrentId() {
        return mCurrentId;
    }

    public String getCurrentTitle() {
        return mTitle;
    }

    public void setActionModeListener(ActionModeListener actionModeListener) {
        mActionModeListener = actionModeListener;
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        mNavigationListener = navigationListener;
    }

    @SuppressLint("CommitTransaction")
    public FragmentTransaction createFragmentTransaction(Fragment fragment) {
        return mFragmentManager.beginTransaction()
                .replace(mContainerId, fragment, CURRENT_TITLE);
    }

    public boolean openDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            return false;
        }
        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    public boolean closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    public static void saveActionModeState(Bundle outState, ActionModeListener callbacks) {
        outState.putBoolean(ACTION_MODE_SUSPENDED, callbacks.isActionModeSuspended());
        outState.putBoolean(ACTION_MODE_ACTIVE, callbacks.isActionModeActive());
    }

    public static Intent createNavigationIntent(@IdRes int res) {
        Intent intent = new Intent();
        intent.putExtra(NAVIGATE_ID, res);
        return intent;
    }

    public interface NavigationListener {
        void onItemSelected(MenuItem item);
    }

    public interface ActionModeListener {
        void onSuspendActionMode();

        void onResumeActionMode();

        boolean isActionModeActive();

        boolean isActionModeSuspended();
    }

}

