package com.medcorp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/5.
 */
public class SettingAboutActivity extends BaseActivity {
    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        toolbarTitle.setText(R.string.title_about);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
