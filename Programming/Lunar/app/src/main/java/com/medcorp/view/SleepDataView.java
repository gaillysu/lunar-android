package com.medcorp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.medcorp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaillysu on 15/8/17.
 */
public class SleepDataView extends View {
    private Paint mPaint;
    private int mRoundColor;
    private int mTextColor;
    private int mWakeSleepColor;
    private int mLightSleepColor;
    private int mDeepSleepColor;
    private float mTextSize;
    private float mRoundWidth;
    private int mMax;
    private int mProgress;
    private boolean mTextIsDisplayable;
    private JSONObject mSleepAnalysisResult = new JSONObject();

    public static final int STROKE = 0;
    public static final int FILL = 1;

    float[] sweep = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    final float SWEEP_INC = 0.5f;
    enum SLEEPSTATUS{
        ACTIVITY,WAKE,LIGHT,DEEP
    }

    List<Float> sleepDegree = new ArrayList<Float>();
    List<Paint> sleepColor = new ArrayList<Paint>();

    public SleepDataView(Context context) {
        this(context, null);
    }

    public SleepDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepDataView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);
        //get custom attributes
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.BLUE);
        mTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 60);
        mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        mMax = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        mTextIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        mWakeSleepColor = mTypedArray.getColor(R.styleable.RoundProgressBar_sleepWakeColor, Color.GREEN);
        mLightSleepColor = mTypedArray.getColor(R.styleable.RoundProgressBar_sleepLightColor, Color.LTGRAY);
        mDeepSleepColor = mTypedArray.getColor(R.styleable.RoundProgressBar_sleepDeepColor, Color.BLUE);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = (int) (Math.min(getWidth()/2,getHeight()/2) - mRoundWidth/2);
        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, mPaint);

        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int)(((float) mProgress / (float) mMax) * 100);
        float textWidth = mPaint.measureText(percent + "%");

        RectF oval = new RectF(getWidth()/2 - radius, getHeight()/2 - radius, getWidth()/2
                + radius, getHeight()/2 + radius);

        int colors[] = {mWakeSleepColor,mLightSleepColor,mDeepSleepColor,Color.TRANSPARENT};
        Paint[] mPaints;
        mPaints = new Paint[colors.length];
        for (int i = 0; i < colors.length; i++) {
            mPaints[i] = new Paint();
            mPaints[i].setAntiAlias(true);
            mPaints[i].setStrokeWidth(mRoundWidth);
            mPaints[i].setStyle(Paint.Style.STROKE);
            mPaints[i].setColor(colors[i]);
        }

        float start = 0;
        float end = 0;
        float initStart = 0;

        if(mSleepAnalysisResult.has("startDateTime") && mSleepAnalysisResult.has("endDateTime"))
        {
            try {
                long startsleep = mSleepAnalysisResult.getLong("startDateTime");
                long endsleep = mSleepAnalysisResult.getLong("endDateTime");
                if(startsleep == 0 || endsleep ==0 || startsleep==endsleep)
                {
                    if(mTextIsDisplayable)
                    canvas.drawText("----", getWidth()/2 - textWidth / 2, getHeight()/2 + mTextSize /2 + 60, mPaint);
                    return;
                }

                String[] hhmm = new SimpleDateFormat("HH:mm").format(new Date(startsleep)).split(":");

                //calculator the start degree:0~360
                start = ( ((Integer.parseInt(hhmm[0]))%12)*60 )/2.0f;
                if(((Integer.parseInt(hhmm[0]))%12)>=3) start = start - 90;
                else {
                    start = start + 270;
                }
                initStart = start;

                hhmm = new SimpleDateFormat("HH:mm").format(new Date(endsleep)).split(":");
                end = ( ((Integer.parseInt(hhmm[0]))%12)*60 + Integer.parseInt(hhmm[1]))/2.0f;
                if(((Integer.parseInt(hhmm[0]))%12)>=3) end = end - 90;
                else {
                    end = end + 270;
                }

                JSONArray hourlyWakeTime = new JSONArray(mSleepAnalysisResult.getString("mergeHourlyWakeTime"));
                JSONArray hourlyLightTime = new JSONArray(mSleepAnalysisResult.getString("mergeHourlyLightTime"));
                JSONArray hourlyDeepTime = new JSONArray(mSleepAnalysisResult.getString("mergeHourlyDeepTime"));

                if(hourlyWakeTime.length() >0 && hourlyLightTime.length() >0 && hourlyDeepTime.length() >0 )
                {
                    long total = 0; //(endsleep - startsleep) / 1000 / 60;
                    int sleepTotal = 0;
                    for(int k = 0; k < hourlyWakeTime.length(); k++){
                        total+= hourlyWakeTime.getInt(k);
                        total+= hourlyLightTime.getInt(k);
                        total+= hourlyDeepTime.getInt(k);
                        sleepTotal+= hourlyLightTime.getInt(k);
                        sleepTotal+= hourlyDeepTime.getInt(k);
                    }

                    percent = (int)(((float) sleepTotal / (float) total) * 100);
                    textWidth = mPaint.measureText(percent + "%");
                    if(mTextIsDisplayable)
                    canvas.drawText(percent + "%", getWidth()/2 - textWidth / 2, getHeight()/2 + mTextSize /2 + 60, mPaint);

                    //draw circle
                    sleepDegree.clear();
                    sleepColor.clear();

                    //if user 's sleep had got broken sometimes, after have a rest and restart one new sleep.
                    //so the circle view should display the rest time as "blank" color
                    //for let the graph keep good continuity,I follow this rule to draw the circle
                    //the sleep order is active/wake/light/deep ,please see @lastHourlySleepStatus
                    SLEEPSTATUS lastHourlySleepStatus = SLEEPSTATUS.ACTIVITY;

                    for (int j = 0; j < hourlyWakeTime.length(); j++)
                    {
                        if(lastHourlySleepStatus == SLEEPSTATUS.ACTIVITY)
                        {
                            sleepDegree.add((60-hourlyWakeTime.getInt(j)-hourlyLightTime.getInt(j)-hourlyDeepTime.getInt(j))/2.0f);
                            sleepColor.add(mPaints[3]);
                            sleepDegree.add(hourlyWakeTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add(hourlyLightTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(hourlyDeepTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[2]);
                            lastHourlySleepStatus = hourlyDeepTime.getInt(j)>0?SLEEPSTATUS.DEEP:(hourlyLightTime.getInt(j)>0?SLEEPSTATUS.LIGHT:(hourlyWakeTime.getInt(j)>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }
                        else if(lastHourlySleepStatus == SLEEPSTATUS.WAKE)
                        {
                            sleepDegree.add((60-hourlyWakeTime.getInt(j)-hourlyLightTime.getInt(j)-hourlyDeepTime.getInt(j))/2.0f);
                            sleepColor.add(mPaints[3]);
                            sleepDegree.add(hourlyWakeTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add(hourlyLightTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(hourlyDeepTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[2]);
                            lastHourlySleepStatus = hourlyDeepTime.getInt(j)>0?SLEEPSTATUS.DEEP:(hourlyLightTime.getInt(j)>0?SLEEPSTATUS.LIGHT:(hourlyWakeTime.getInt(j)>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }
                        else if(lastHourlySleepStatus == SLEEPSTATUS.LIGHT)
                        {
                            sleepDegree.add(hourlyLightTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(hourlyDeepTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[2]);
                            sleepDegree.add(hourlyWakeTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add((60-hourlyWakeTime.getInt(j)-hourlyLightTime.getInt(j)-hourlyDeepTime.getInt(j))/2.0f);
                            sleepColor.add(mPaints[3]);
                            lastHourlySleepStatus = hourlyDeepTime.getInt(j)>0?SLEEPSTATUS.DEEP:(hourlyLightTime.getInt(j)>0?SLEEPSTATUS.LIGHT:(hourlyWakeTime.getInt(j)>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }
                        else if(lastHourlySleepStatus == SLEEPSTATUS.DEEP)
                        {
                            sleepDegree.add(hourlyDeepTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[2]);
                            sleepDegree.add(hourlyLightTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(hourlyWakeTime.getInt(j)/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add((60-hourlyWakeTime.getInt(j)-hourlyLightTime.getInt(j)-hourlyDeepTime.getInt(j))/2.0f);
                            sleepColor.add(mPaints[3]);
                            lastHourlySleepStatus = hourlyDeepTime.getInt(j)>0?SLEEPSTATUS.DEEP:(hourlyLightTime.getInt(j)>0?SLEEPSTATUS.LIGHT:(hourlyWakeTime.getInt(j)>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }
                    }//end for
                    start = initStart;
                    int i =0;
                    for(Float f:sleepDegree)
                    {
                        //fix a bug that some phones like :Nexus 5x and xiaomi drawArc is wrong, but Nexus 5 and S4/s5 drawArc  well.
                        if(sweep[i]>0)
                        {
                            canvas.drawArc(oval, start, sweep[i], false, sleepColor.get(i));
                        }
                        start += f;
                        if (sweep[i] < f) {
                            sweep[i] += SWEEP_INC;
                        }
                        i = i+1;
                    }
                }

                //start/end/total
                mPaint.setColor(Color.WHITE);
                mPaint.setTypeface(Typeface.DEFAULT);
                mPaint.setTextSize(24f);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            if(mTextIsDisplayable)
            canvas.drawText("----", getWidth()/2 - textWidth / 2, getHeight()/2 + mTextSize /2 + 60, mPaint);
        }
        invalidate();
    }

    public synchronized void setSleepAnalysisResult(JSONObject result)
    {
        mSleepAnalysisResult = result;
        postInvalidate();
    }

}
