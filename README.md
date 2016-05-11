# NavigationViewManager
A simple helper library for NavigationView to help manage navigation logic and reduce boilerplate in your MainActivity.

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
    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_camera));
}
```

#####3. Override onNavigationItemSelected to replace your content fragments.

```java
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
