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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.rubensousa.navigationviewmanager.NavigationViewManager;


public class DummyFragment extends Fragment
        implements NavigationViewManager.ActionModeListener, View.OnClickListener,
        ActionMode.Callback {

    public static final String TITLE = "title";

    private String mTitle;
    private ActionMode mActionMode;
    private boolean mActionModeSuspended = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString(TITLE);
        } else {
            mTitle = "Placeholder";
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        view.findViewById(R.id.actionModeButton).setOnClickListener(this);
        Button button = (Button) view.findViewById(R.id.intentButton);
        // Disable intent button if it's the import fragment
        if (mTitle.equals("Import")) {
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(this);
        }
        textView.setText(mTitle);

        ((MainActivity) getActivity()).setupToolbar(toolbar);

        if (savedInstanceState != null) {
            mActionModeSuspended
                    = savedInstanceState.getBoolean(NavigationViewManager.ACTION_MODE_SUSPENDED);

            if (savedInstanceState.getBoolean(NavigationViewManager.ACTION_MODE_ACTIVE)) {
                // restore action mode data here
                mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        NavigationViewManager.saveActionModeState(outState, this);
    }

    @Override
    public void onSuspendActionMode() {
        if (mActionMode != null) {
            // save ActionMode state here
            mActionMode.finish();
            mActionMode = null;
            mActionModeSuspended = true;
        }
    }

    @Override
    public void onResumeActionMode() {
        mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
        mActionModeSuspended = false;
        // restore action mode state here
    }

    @Override
    public boolean isActionModeActive() {
        return mActionMode != null;
    }

    @Override
    public boolean isActionModeSuspended() {
        return mActionModeSuspended;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.actionModeButton) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
        }

        if (v.getId() == R.id.intentButton) {
            // Change navigation fragments with a custom intent as argument
            Intent intent = new Intent();
            intent.putExtra("argument", "dummy");
            ((MainActivity) getActivity()).navigateWithIntent(R.id.nav_import, intent);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
    }
}
