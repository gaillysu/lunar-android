package com.medcorp.lunar.util;

import android.support.design.widget.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/5.
 */
public class EmailUtils {

    private static Snackbar snackbar = null;

    public static boolean checkEmail(String email) {
        String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }


}
