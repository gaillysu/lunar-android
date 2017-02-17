package com.medcorp.view.fontstrategy;


import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Karl on 9/29/15.
 */
public interface FontStrategy {

    public void execute(Button button);

    public void execute(EditText editText);

    public void execute(TextView textView);
}
