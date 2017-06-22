package com.medcorp.lunar.activity.tutorial;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.util.PublicUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage2Activity extends BaseActivity {

    @Bind(R.id.activity_tutorial_page2_notice_textview)
    TextView describeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_2);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        if (!PublicUtils.isLocaleChinese()) {
            SpannableString ss = new SpannableString(getString(R.string.tutorial_shipping_mode_describe_text));
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
            ForegroundColorSpan span2 = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
            ForegroundColorSpan span3 = new ForegroundColorSpan(Color.WHITE);
            String describe = getString(R.string.tutorial_shipping_mode_describe_text);
            int index = describe.indexOf(getString(R.string.other_color_turquoise));
            int white = describe.indexOf(getString(R.string.other_color_white));
            int indexTv = describe.indexOf(getString(R.string.other_color_text));
            ss.setSpan(span, index, index + getString(R.string.other_color_turquoise).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(span2, white, white +getString(R.string.other_color_white).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(span3, indexTv, indexTv + getString(R.string.other_color_text).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            describeTv.setText(ss);
        }
    }

    @OnClick(R.id.activity_tutorial_2_continue_button)
    public void continueClicked() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivity(TutorialPage3Activity.class);
            finish();
        } else {
            new MaterialDialog.Builder(this)
                    .content(R.string.tutorial_2_dialog_positive)
                    .positiveText(android.R.string.ok)
                    .negativeText(R.string.tutorial_2_dialog_negative)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                        }
                    }).show();
        }
    }
}
