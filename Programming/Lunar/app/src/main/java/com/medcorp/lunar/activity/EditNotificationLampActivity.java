package com.medcorp.lunar.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.EditLunarNotificationAdapter;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.model.color.LedLamp;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.Notification;
import com.medcorp.lunar.util.Preferences;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/12/8.
 */

public class EditNotificationLampActivity extends BaseActivity {

    @Bind(R.id.all_lunar_notification_lamp_list)
    SwipeMenuRecyclerView allNotificationLampList;

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    private EditLunarNotificationAdapter mEditItemAdapter;
    private List<LedLamp> userSettingAllLamp;
    private ApplicationModel mModel;
    private Notification notification;
    private NevoLed nevoLed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_lunar_notification_lamp_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.edit_notification_item_name);
        mModel = getModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        notification = (Notification) getIntent().getExtras().getSerializable(getString(R.string.key_notification));
        nevoLed = Preferences.getNotificationColor(this, notification, getModel());
        initView();
    }

    private void initView() {
        userSettingAllLamp = mModel.getAllLedLamp();
        allNotificationLampList.setLayoutManager(new LinearLayoutManager(this));
        allNotificationLampList.setHasFixedSize(true);
        allNotificationLampList.setItemAnimator(new DefaultItemAnimator());
        allNotificationLampList.setSwipeMenuCreator(swipeMenuCreator);
        allNotificationLampList.setSwipeMenuItemClickListener(menuItemClickListener);
        mEditItemAdapter = new EditLunarNotificationAdapter(userSettingAllLamp,nevoLed.getHexColor());
        allNotificationLampList.setAdapter(mEditItemAdapter);
        mEditItemAdapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_menu).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
                openChooseColor();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openChooseColor() {
        Intent intent = new Intent(EditNotificationLampActivity.this, EditNotificationAttributeActivity.class);
        intent.putExtra("isEdit", false);
        intent.putExtra("name", getResources().getString(R.string.notification_def_name));
        startActivity(intent);
    }


    /**
     * item's click event save data
     */
    private EditLunarNotificationAdapter.OnItemClickListener onItemClickListener = new EditLunarNotificationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Preferences.saveNotificationColor(EditNotificationLampActivity.this, notification, userSettingAllLamp.get(position).getColor());
            //show current selected color
            mEditItemAdapter.setColor(userSettingAllLamp.get(position).getColor());
            mEditItemAdapter.notifyDataSetChanged();
        }
    };


    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = getResources().getDimensionPixelSize(R.dimen.edit_notification_item_width);
            SwipeMenuItem deleteItem = new SwipeMenuItem(EditNotificationLampActivity.this)
                    .setBackgroundDrawable(R.drawable.selector_red)
                    .setText(getString(R.string.edit_notification_delete_item))
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);

            SwipeMenuItem closeItem = new SwipeMenuItem(EditNotificationLampActivity.this)
                    .setBackgroundDrawable(R.drawable.selector_purple)
                    .setText(getString(R.string.edit_notification_item_name))
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem);
        }
    };

    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();
            if (menuPosition == 0) {
                LedLamp ledLamp = userSettingAllLamp.get(adapterPosition);
                mModel.removeLedLamp(ledLamp.getName(),ledLamp.getColor());
                userSettingAllLamp.remove(adapterPosition);
                mEditItemAdapter.notifyItemRemoved(adapterPosition);
            }
            if (menuPosition == 1) {
                LedLamp ledLamp = userSettingAllLamp.get(adapterPosition);
                if (ledLamp != null) {
                    Intent intent = new Intent(EditNotificationLampActivity.this, EditNotificationAttributeActivity.class);
                    intent.putExtra("name", ledLamp.getName());
                    intent.putExtra("color",ledLamp.getColor());
                    intent.putExtra("isEdit", true);
                    startActivity(intent);
                }
            }
        }
    };
}
