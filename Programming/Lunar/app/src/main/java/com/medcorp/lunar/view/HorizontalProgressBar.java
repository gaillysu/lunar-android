package com.medcorp.lunar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.medcorp.lunar.R;

/***
 * Created by Jason on 2017/6/23.
 */

public class HorizontalProgressBar extends ProgressBar {
    private final static int DEFAULT_TEXT_SIZE = 10;//sp
    private final static int DEFAULT_TEXT_COLOR = 0x00000000;
    private final static int DEFAULT_TEXT_OFFSET = 10;//dp
    private final static int DEFAULT_REACH_COLOR = DEFAULT_TEXT_COLOR;
    private final static int DEFAULT_REACH_HIGHT = 2;//dp
    private final static int DEFAULT_UNREACH_COLOR = 0x00000000;
    private final static int DEFAULT_UNREACH_HIGHT = 2;//dp
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);
    protected int mReachColor = DEFAULT_REACH_COLOR;
    protected int mReachHeight = dp2px(DEFAULT_REACH_HIGHT);
    protected int mUnReachColor = DEFAULT_UNREACH_COLOR;
    protected int mUnReachHeight = dp2px(DEFAULT_UNREACH_HIGHT);
    protected Paint mPaint = new Paint();
    protected int mRealWidth;

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(attrs);

    }

    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs,
                R.styleable.HorizontalProgressBar);
        mTextSize = (int) ta.getDimension(R.styleable.
                HorizontalProgressBar_progress_text_size, mTextSize);
        mTextColor = ta.getColor(R.styleable.
                HorizontalProgressBar_progress_text_color, mTextColor);
        mTextOffset = (int) ta.getDimension(R.styleable.
                HorizontalProgressBar_progress_text_offset, mTextOffset);
        mReachHeight = (int) ta.getDimension(R.styleable.
                HorizontalProgressBar_progress_reach_height, mReachHeight);
        mReachColor = ta.getColor(R.styleable.
                HorizontalProgressBar_progress_reach_color, mReachColor);
        mUnReachHeight = (int) ta.getDimension(R.styleable.
                HorizontalProgressBar_progress_unreach_height, mUnReachHeight);
        mUnReachColor = ta.getColor(R.styleable.
                HorizontalProgressBar_progress_unreach_color, mUnReachColor);
        ta.recycle();
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal, height);
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom() + Math.
                    max(Math.max(mReachHeight, mUnReachHeight), Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }

        }
        return result;

    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        boolean noNeedUnRech = false;
        //draw reach bar
        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mRealWidth;
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnRech = true;
        }
        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }
        //draw text
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);
        //draw unReach bar
        if (!noNeedUnRech) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }
        canvas.restore();
    }


    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());

    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }

}