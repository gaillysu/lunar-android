package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.model.color.LedLamp;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason on 2016/12/9.
 */

public class EditNotificationAttributeActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.edit_notification_lamp_iv)
    ImageView showSelectColor;
    @Bind(R.id.edit_notification_name_text_view)
    TextView mNameTv;
    @Bind(R.id.color_picker_view)
    ColorPickerView colorPickerView;
    private String name;
    private int color;
    private LedLamp mLedLamp;
    private int mLedId;
    private boolean isEdit = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_notification_attribute_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.edit_notification_item_name);
        initView();
        registerClick();
    }

    private void registerClick() {
        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                showSelectColor.setColorFilter(selectedColor);
                color = selectedColor;
            }
        });
    }

    private void initView() {
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("isEdit", false);
        if (isEdit) {
            mLedId = intent.getIntExtra("id", -1);
            mLedLamp = getModel().getSelectLamp(mLedId);
            showSelectColor.setColorFilter(mLedLamp.getColor());
            name = mLedLamp.getName();
        } else {
            name = intent.getStringExtra("name");
        }
        mNameTv.setText(name);
    }

    @OnClick(R.id.edit_nt_name_ll)
    public void editName() {
        new MaterialDialog.Builder(this).title(getString(R.string.add_new_notification_dialog_title))
                .content(getString(R.string.add_new_notification_dialog_hint_content))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input.length() == 0) {
                            name = getString(R.string.new_nt_def_name);
                        } else {
                            name = input.toString();
                        }
                        mNameTv.setText(name);
                    }
                }).negativeText(R.string.goal_cancel).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.done_menu).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_menu:
                if (isEdit) {
                    mLedLamp.setSelect(mLedLamp.isSelect());
                } else {
                    mLedLamp = new LedLamp();
                    mLedLamp.setSelect(false);
                }
                mLedLamp.setName(name);
                mLedLamp.setColor(color);
                getModel().upDataLedLamp(mLedLamp);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
