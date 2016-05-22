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
    compile 'com.github.rubensousa:NavigationViewManager:0.1'
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
    Fragment fragment = null;

    switch (item) {
        case R.id.nav_import:
            fragment = new ImportFragment();
            break;
        case R.id.nav_gallery:
            fragment = new GalleryFragment();
            break;
        default:
            fragment = new DummyFragment();
    }
    
    return fragment;
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
            navigationView, drawer,R.id.containerLayout);

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
```

##### 5. (Optional) Override createFragmentTransaction(Fragment fragment) to create your own FragmentTransactions

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
