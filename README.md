# NavigationViewManager
A simple helper library to separate navigation logic from your MainActivity and reduce boilerplate.

It also includes ActionMode flow control when the NavigationView is shown/hidden.

Check the sample app.

### Minimum API: 14

## Build

Add the following to your build.gradle:

```groovy
repositories{
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.rubensousa:NavigationViewManager:0.3'
}
```

## How to use

#####1. Create your NavigationViewManagerImpl class that extends from NavigationViewManager.

#####2. Implement showDefaultItem to show a default fragment on first start.

```java
@Override
public void showDefaultItem(NavigationView navigationView) {
    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_import));
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
private NavigationViewManager mNavigationViewManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

    mNavigationViewManager = new NavigationViewManagerImpl(getSupportFragmentManager(),
            navigationView, drawer, R.id.containerLayout);

    mNavigationViewManager.init(savedInstanceState, getIntent());
}

// This is useful to navigate when you receive an intent
// from a push notification
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    mNavigationViewManager.navigateWithIntent(intent);
}

@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mNavigationViewManager.onSaveInstanceState(outState);
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
```

##### 5. To navigate with Intents:

```java
Intent intent = NavigationViewManager.createNavigationIntent(R.id.menuId);
intent.putExtra("argument", "dummy");
mNavigationViewManager.navigateWithIntent(intent);
```

The intent will be automatically passed to your fragment

##### 6. (Optional) Override createFragmentTransaction(Fragment fragment) to create your own FragmentTransactions

```java
@Override
public FragmentTransaction createFragmentTransaction(Fragment fragment) {
    return mFragmentManager.beginTransaction()
            .replace(R.id.frameLayout,fragment,CURRENT_TITLE)
            .addToBackStack(null);
}
```
The default transaction is:

```java
public FragmentTransaction createFragmentTransaction(Fragment fragment) {
    return mFragmentManager.beginTransaction()
            .replace(mContainerId, fragment, CURRENT_TITLE);
}
```
## ActionMode pausing/resuming

When the NavigationView opens, you should finish or at least pause the ActionMode.
To do this, you can implement NavigationViewManager.ActionModeListener in your fragments and then:

```java
private ActionMode mActionMode;
private boolean mActionModeSuspended = false;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // ...
    
    if (savedInstanceState != null) {
        mActionModeSuspended
                = savedInstanceState.getBoolean(NavigationViewManager.ACTION_MODE_SUSPENDED);

        // Restore action mode state if it was active before
        if (savedInstanceState.getBoolean(NavigationViewManager.ACTION_MODE_ACTIVE)) {
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
