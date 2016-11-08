# NavigationManager
A simple helper library to separate navigation logic from your MainActivity and reduce boilerplate.

It also includes ActionMode flow control when the NavigationView is shown/hidden.

Check the sample app.

### Minimum API: 14

## Build

Add the following to your build.gradle:

```groovy
dependencies {
    compile 'com.github.rubensousa:navigationmanager:1.1'
}
```

## How to use

#####1. Create your NavigationManager class that extends from NavigationManager.

#####2. Implement getDefaultItem to show a default fragment on first start.

```java
@Override
public int getDefaultItem() {
    return R.id.nav_import;
}
```

##### 3. Implement createFragment to replace your content fragments.

```java
@NonNull
@Override
public Fragment createFragment(@IdRes int item) {
    switch (item) {
        case R.id.nav_import:
            return new ImportFragment();
        case R.id.nav_gallery:
            return new GalleryFragment();
        default:
            return new DummyFragment();
    }
}
```

#####4. Add the following to your MainActivity:

```java
private NavigationManager mNavigationManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

    mNavigationManager = new NavigationViewManager(getSupportFragmentManager(),
            navigationView, drawer, R.id.containerLayout);

    mNavigationManager.init(savedInstanceState, getIntent());
}

// This is useful to navigate when you receive an intent
// from a push notification
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    mNavigationManager.navigateWithIntent(intent);
}

@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mNavigationManager.onSaveInstanceState(outState);
}

@Override
public void onBackPressed() {
    if (!mNavigationManager.closeDrawer()) {
        super.onBackPressed();
    }
}
```

##### 5. To navigate with Intents:

```java
Intent intent = NavigationManager.createNavigationIntent(R.id.menuId);
intent.putExtra("argument", "dummy");
mNavigationManager.navigateWithIntent(intent);
```

The intent will be automatically passed to your fragment

### Optional Usage

##### 1. Override createFragmentTransaction(Fragment fragment) to create your own FragmentTransactions

The default transaction is:

```java
public FragmentTransaction createFragmentTransaction(Fragment fragment) {
    return mFragmentManager.beginTransaction()
            .replace(mContainerId, fragment, CURRENT_TITLE);
}
```
##### 2. Override commitFragmentTransaction(FragmentTransaction)

The default commit is:

```java
public void commitFragmentTransaction(FragmentTransaction transaction) {
    // We can allow state loss because the fragment will start for the first time
    transaction.commitAllowingStateLoss();
}
```
##### 3. Override shouldDelayTransaction to enable/disable the delay between switching fragments

## ActionMode pausing/resuming

When the NavigationView opens, you should finish or at least pause the ActionMode.
To do this, you can implement NavigationManager.ActionModeListener in your fragments and then:

```java
private ActionMode mActionMode;
private boolean mActionModeSuspended = false;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // ...
    
    if (savedInstanceState != null) {
        mActionModeSuspended
                = savedInstanceState.getBoolean(NavigationManager.ACTION_MODE_SUSPENDED);

        // Restore action mode state if it was active before
        if (savedInstanceState.getBoolean(NavigationManager.ACTION_MODE_ACTIVE)) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
            // restore action mode data here
        }
    }
    return view;
}

@Override
public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // Save actionMode state. This will internally check if the ActionMode is active
    // or suspended by calling isActionModeActive or isActionModeSuspended
    NavigationManager.saveActionModeState(outState, this);
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
```
## License

    Copyright 2016 Rúben Sousa
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
