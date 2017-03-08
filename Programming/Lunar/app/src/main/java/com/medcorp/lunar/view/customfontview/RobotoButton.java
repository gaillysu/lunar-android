package com.medcorp.lunar.view.customfontview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.medcorp.lunar.R;
import com.medcorp.lunar.view.fontstrategy.RobotoBoldFontStrategy;
import com.medcorp.lunar.view.fontstrategy.RobotoFontStrategy;
import com.medcorp.lunar.view.fontstrategy.RobotoLightFontStrategy;
import com.medcorp.lunar.view.fontstrategy.RobotoThinFontStrategy;


/**
 * Created by Karl on 9/29/15.
 */
public class RobotoButton extends Button {

    public RobotoButton(Context context) {
        super(context);
    }

    public RobotoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontAttr);
        changeFontStyle(context, a);
    }

    public RobotoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontAttr, defStyleAttr, 0);
        changeFontStyle(context, a);
    }

    public void changeFontStyle(Context context, TypedArray typedArray){
        if(typedArray.getBoolean(R.styleable.CustomFontAttr_thin,false)){
            new RobotoThinFontStrategy(context).execute(this);
        }else if(typedArray.getBoolean(R.styleable.CustomFontAttr_light,false)){
            new RobotoLightFontStrategy(context).execute(this);
        }else if(typedArray.getBoolean(R.styleable.CustomFontAttr_bold,false)){
            new RobotoBoldFontStrategy(context).execute(this);
        }else{
            new RobotoFontStrategy(context).execute(this);
        }
    }
}
