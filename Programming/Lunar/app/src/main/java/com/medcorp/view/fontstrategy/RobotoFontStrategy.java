package com.medcorp.view.fontstrategy;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.medcorp.R;

/**
 * Created by karl-john on 14/12/15.
 */
public class RobotoFontStrategy implements FontStrategy {

    private Typeface tf;
    public RobotoFontStrategy(Context context) {
        tf = Typeface.createFromAsset(context.getAssets(),
                context.getString(R.string.font_roboto_regular_path));
    }

    @Override
    public void execute(Button button) {
        button.setTypeface(tf);
    }

    @Override
    public void execute(EditText editText) {
        editText.setTypeface(tf);
    }

    @Override
    public void execute(TextView textView) {
        textView.setTypeface(tf);
    }
}
