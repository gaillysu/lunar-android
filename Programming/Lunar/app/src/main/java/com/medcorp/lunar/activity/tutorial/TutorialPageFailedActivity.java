package com.medcorp.lunar.activity.tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.util.PublicUtils;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Karl on 1/19/16.
 */
public class TutorialPageFailedActivity extends BaseActivity {

    @Bind(R.id.activity_tutorial_page5_search_failure_tip_textview)
    RobotoTextView describeTv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_failed);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        if (!PublicUtils.isLocaleChinese()) {
            SpannableString ss = new SpannableString(getString(R.string.tutorial_failed_text));
            ForegroundColorSpan span = new ForegroundColorSpan(Color.WHITE);
            ForegroundColorSpan span2 = new ForegroundColorSpan(Color.WHITE);
            ForegroundColorSpan span3 = new ForegroundColorSpan(Color.WHITE);
            String text = getString(R.string.tutorial_failed_text);
            int index = text.indexOf(getString(R.string.other_color_bluetooth_failed));
            int index2 = text.indexOf(getString(R.string.other_color_led_text));
            int index3 = text.indexOf(getString(R.string.other_color_blinking));
            ss.setSpan(span, index, index + getString(R.string.other_color_bluetooth_failed).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(span2, index2, index2 + getString(R.string.other_color_led_text).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(span3, index3, index3 + getString(R.string.other_color_blinking).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            describeTv.setText(ss);
        }
    }

    @OnClick(R.id.activity_tutorial_retry_button)
    public void retryClicked() {
        finish();
    }
}
