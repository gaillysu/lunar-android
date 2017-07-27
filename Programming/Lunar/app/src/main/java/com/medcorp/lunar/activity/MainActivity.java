package com.medcorp.lunar.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.bluetooth.OnSyncEvent;
import com.medcorp.lunar.fragment.AlarmFragment;
import com.medcorp.lunar.fragment.AnalysisFragment;
import com.medcorp.lunar.fragment.HomeClockFragment;
import com.medcorp.lunar.fragment.MainFragment;
import com.medcorp.lunar.fragment.SettingsFragment;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.PublicUtils;

import net.medcorp.library.ble.event.BLEBluetoothOffEvent;
import net.medcorp.library.ble.event.BLEConnectionStateChangedEvent;
import net.medcorp.library.ble.event.BLESearchEvent;
import net.medcorp.library.ble.util.Optional;
import net.medcorp.library.permission.PermissionRequestDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

import static com.medcorp.lunar.R.id.navigation_header_imageview;
import static com.medcorp.lunar.util.Preferences.getUserHeardPicturePath;


/***
 * Created by Karl on 12/10/15.
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_main_drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.overview_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.activity_main_navigation_view)
    NavigationView navigationView;

    private TextView showUserFirstNameText;

    private View rootView;
    private TextView userView;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private MenuItem selectedMenuItem;
    private Optional<BaseObservableFragment> activeFragment;
    private FragmentManager fragmentManager;
    private Snackbar snackbar = null;
    private boolean bigSyncStart = false;
    private BaseObservableFragment mainStepsFragment;
    private User currentUser;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activeFragment = new Optional<>();
        rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        navigationView.setNavigationItemSelectedListener(this);
        ColorStateList colorStateList = this.getResources().getColorStateList(R.color.navigation_text_color_select);
        navigationView.setItemTextColor(colorStateList);
        drawerLayout.setDrawerListener(this);
        MenuItem firstItem = navigationView.getMenu().getItem(0);
        onNavigationItemSelected(firstItem);
        firstItem.setChecked(true);
        mainStepsFragment = MainFragment.instantiate(this, MainFragment.class.getName());
        activeFragment.set(mainStepsFragment);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        if (fragmentManager.getBackStackEntryCount() == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_frame_layout, mainStepsFragment)
                    .commit();
        }

        View headerView = navigationView.getHeaderView(0);
        userView = (TextView) headerView.findViewById(R.id.navigation_header_textview);
        showUserFirstNameText = (TextView) headerView.findViewById(R.id.drawable_left_show_user_name_tv);
        final ImageButton userImageView = (ImageButton) headerView.findViewById(navigation_header_imageview);
        floatingActionButton = (FloatingActionButton) headerView.findViewById(R.id.navigation_header_spinner);
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                initView(userImageView,user);
                currentUser = user;
            }
        });

    }

    private void initView(ImageButton userImageView ,User user) {
        String userEmail = null;
        if (user.isLogin()) {
            userEmail = user.getUserEmail();
        } else {
            userEmail = getString(R.string.watch_med_profile);
        }
        String userHeardPicturePath = getUserHeardPicturePath(this, userEmail);
        if (userHeardPicturePath != null) {
            Bitmap bt = BitmapFactory.decodeFile(userHeardPicturePath);
            if (bt != null) {
                userImageView.setImageBitmap(PublicUtils.drawCircleView(bt));
            }
        } else {
            userImageView.setImageResource(R.drawable.user);
        }
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
            }
        });

        if (user.isLogin()) {
            floatingActionButton.setVisibility(View.GONE);
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra(getString(R.string.open_activity_is_tutorial), false);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getModel().getSyncController().getBluetoothStatus() == BluetoothAdapter.STATE_OFF) {
            showStateString(R.string.in_app_notification_bluetooth_disabled, false);
        }
        if (!getModel().isWatchConnected()) {
            getModel().startConnectToWatch(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showStateString(int id, boolean dismiss) {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }

        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setText(getString(id));
        Snackbar.SnackbarLayout ve = (Snackbar.SnackbarLayout) snackbar.getView();
        ve.setBackgroundColor(getResources().getColor(R.color.snackbar_bg_color));
        snackbar.show();

        if (dismiss) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!bigSyncStart) {
                        snackbar.dismiss();
                    }
                }
            }, 1800);
        }
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        userView.setText(currentUser.getUserEmail());
        showUserFirstNameText.setText(currentUser.isLogin() ?
                (currentUser.getFirstName() != null ?currentUser.getFirstName() : "") +
                        " " + (currentUser.getLastName() != null ?
                        currentUser.getLastName() : "") : "");
    }


    @Override
    public void onDrawerClosed(View drawerView) {
        setFragment(selectedMenuItem);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        toolbar.setTitle(getString(R.string.title_steps));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void setFragment(MenuItem item) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        BaseObservableFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_steps_fragment:
                fragment = MainFragment.instantiate(MainActivity.this, MainFragment.class.getName());
                toolbar.setTitle(item.getTitle());
                break;
            case R.id.nav_alarm_fragment:
                fragment = AlarmFragment.instantiate(MainActivity.this, AlarmFragment.class.getName());
                toolbar.setTitle(item.getTitle());
                break;
            case R.id.nav_sleep_fragment:
                fragment = AnalysisFragment.instantiate(MainActivity.this, AnalysisFragment.class.getName());
                toolbar.setTitle(item.getTitle());
                break;
            case R.id.nav_settings_fragment:
                fragment = SettingsFragment.instantiate(MainActivity.this, SettingsFragment.class.getName());
                toolbar.setTitle(item.getTitle());
                break;
            case R.id.nav_world_clock:
                fragment = HomeClockFragment.instantiate(MainActivity.this, HomeClockFragment.class.getName());
                toolbar.setTitle(item.getTitle());
                break;
        }


        if (activeFragment.get().getClass().getName().equals(fragment.getClass().getName())) {
            return;
        }
        activeFragment.set(fragment);
        {
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                fragment.setEnterTransition(new Fade().setDuration(300));
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().
                    replace(R.id.activity_main_frame_layout, fragment);

            if (fragmentManager.getBackStackEntryCount() == 0) {
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedMenuItem = item;
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() >= 1) {
            fragmentManager.popBackStack();
            MenuItem item = navigationView.getMenu().getItem(0);
            selectedMenuItem = item;
            item.setChecked(true);
            activeFragment.set((BaseObservableFragment) fragmentManager.getFragments().get(0));
        } else if (fragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        getMenuInflater().inflate(R.menu.menu_choose_goal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackStackChanged() {
        Log.w("Karl", "On backstack changed. current =  " + fragmentManager.getBackStackEntryCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activeFragment.notEmpty()) {
            activeFragment.get().onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 1) {
            Log.w("Karl", "result code = " + resultCode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(OnSyncEvent event) {
        switch (event.getStatus()) {
            case STOPPED:
                bigSyncStart = false;
                showStateString(R.string.in_app_notification_synced, true);
                break;
            case STARTED:
                bigSyncStart = true;
                showStateString(R.string.in_app_notification_syncing, false);
                break;
        }
    }

    @Subscribe
    public void onEvent(BLEConnectionStateChangedEvent event) {
        if (event.isConnected()) {
            showStateString(R.string.in_app_notification_found_nevo, false);
        } else {
            showStateString(R.string.in_app_notification_nevo_disconnected, false);
        }
    }

    @Subscribe
    public void onEvent(BLEBluetoothOffEvent event) {
        showStateString(R.string.in_app_notification_bluetooth_disabled, false);
    }

    @Subscribe
    public void onEvent(BLESearchEvent event) {
        switch (event.getSearchEvent()) {
            case ON_SEARCHING:
                PermissionRequestDialogBuilder builder = new PermissionRequestDialogBuilder(this);
                builder.addPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                builder.addPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                builder.setText(R.string.location_access_content);
                builder.setTitle(R.string.location_access_title);
                builder.askForPermission(this, 1);
                showStateString(R.string.in_app_notification_searching, false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}