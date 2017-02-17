package com.medcorp.view;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by karl-john on 20/11/15.
 */
public class ToastHelper {

    public static void showLongToast(Context context, String msg){
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLongToast(Context context, int id){
        Toast toast = Toast.makeText(context, context.getText(id), Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showShortToast(Context context, String msg){
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showShortToast(Context context, int id){
        Toast toast = Toast.makeText(context, context.getText(id), Toast.LENGTH_SHORT);
        toast.show();
    }
}
