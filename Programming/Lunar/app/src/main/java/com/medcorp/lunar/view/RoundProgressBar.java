package com.medcorp.lunar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.medcorp.lunar.R;


/**
 * Custom Circle Progress Bar in WelcomeFragment
 */
public class RoundProgressBar extends View {
    private Paint mPaint;
    private int mRoundColor;
    private int mRoundProgressColor;
    private int mTextColor;
    private float mTextSize;
    private float mRoundWidth;
    private int mMax;
    private int mProgress;
    private int mValue;
    private boolean mTextIsDisplayable;
    private int mStyle;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();


        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //get custom attributes
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        mRoundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.BLUE);
        mTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 60);
        mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        mMax = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        mTextIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        mStyle = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * draw outside circle
         */
        //int centre = getWidth()/2;
        int radius = (int) (Math.min(getWidth()/2,getHeight()/2) - mRoundWidth/2);//(int) (centre - mRoundWidth /2);
        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, mPaint);

        
        /**
         * draw mProgress
         */
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int)(((float) mProgress / (float) mMax) * 100);
        float textWidth = mPaint.measureText(percent + "%");

        if(mTextIsDisplayable && mStyle == STROKE){
            if(mValue<0) {
                textWidth = mPaint.measureText("----");
                canvas.drawText("----", getWidth() / 2 - textWidth / 2, getHeight() / 2 + mTextSize / 2 + 60, mPaint);
            }
            else {
                String s = percent + "%" + ", "+mValue;
                textWidth = mPaint.measureText(s);
                canvas.drawText(s, getWidth() / 2 - textWidth / 2, getHeight() / 2 + mTextSize / 2 + 60, mPaint);
            }
        }

        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setColor(mRoundProgressColor);
        RectF oval = new RectF(getWidth()/2 - radius, getHeight()/2 - radius, getWidth()/2
                + radius, getHeight()/2 + radius);
        switch (mStyle) {
            case STROKE:{
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, -60, 360 * mProgress / mMax, false, mPaint);
                break;
            }
            case FILL:{
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(mProgress !=0)
                    canvas.drawArc(oval, -60, 360 * mProgress / mMax, true, mPaint);
                break;
            }
        }

    }

    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("mMax not less than 0");
        }
        this.mMax = max;
    }

    public synchronized void setProgress(int progress) {
        if(progress < 0){
            throw new IllegalArgumentException("mProgress not less than 0");
        }
        if(progress > mMax){
            progress = mMax;
        }
        if(progress <= mMax){
            this.mProgress = progress;
            postInvalidate();
        }

    }

    public synchronized void setProgressWithValue(int progress,int value) {
        if(progress < 0){
            throw new IllegalArgumentException("mProgress not less than 0");
        }
        if(progress > mMax){
            progress = mMax;
        }
        if(progress <= mMax){
            this.mProgress = progress;
            this.mValue = value;
            postInvalidate();
        }

    }

}
