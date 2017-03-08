package com.medcorp.lunar.view;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by karl-john on 15/1/16.
 */
public class SnackbarHandler {

    public static Snackbar showSnackbar(View view, String message, int duration){
        Snackbar snackbar =Snackbar.make( view, message, duration);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showLongSnackbar(View view, String message){
        return showSnackbar(view, message, Snackbar.LENGTH_LONG);
    }

    public static Snackbar showShortSnackbar(View view, String message){
        return showSnackbar(view, message, Snackbar.LENGTH_SHORT);
    }

    public static Snackbar showInfiniteSnackbar(View view, String message){
        return showSnackbar(view, message, Snackbar.LENGTH_INDEFINITE);
    }

}
