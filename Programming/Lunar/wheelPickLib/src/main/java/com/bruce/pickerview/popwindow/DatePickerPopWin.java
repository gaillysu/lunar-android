package com.bruce.pickerview.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bruce.pickerview.LoopListener;
import com.bruce.pickerview.LoopView;
import com.bruce.pickerview.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * PopWindow for Date Pick
 */
public class DatePickerPopWin extends PopupWindow implements OnClickListener {

    private static final int DEFAULT_MIN_YEAR = 1900;
    //    public Button cancelBtn;
    //    public Button confirmBtn;
    public LoopView leftLoopView;
    public LoopView middleLoopView;
    public LoopView rightLoopView;
    public View pickerContainerV;
    public View contentView;//root view
    public TextView tv_pickerRight;


    private int minYear; // min year
    private int maxYear; // max year
    private int leftPos = 0;
    private int middlePos = 0;
    private int rightPos = 0;
    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;//text btnTextsize of cancel and confirm button
    private int viewTextSize;
    private int viewStyle;

    List<String> leftList = new ArrayList();
    List<String> middleList = new ArrayList();
    List<String> rightList = new ArrayList();
    List<String> heightList = new ArrayList<>();
    List<String> weightListPoint = new ArrayList<>();
    List<String> weightList = new ArrayList<>();

    public static class Builder {

        //Required
        private Context context;
        private OnDatePickedListener listener;

        public Builder(Context context, OnDatePickedListener listener) {
            this.context = context;
            this.listener = listener;
        }

        //Option
        private int minYear = DEFAULT_MIN_YEAR;
        private int maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private String dateChose = getStrDate();
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;//text btnTextsize of cancel and confirm button
        private int viewTextSize = 25;
        private int viewStyle = 1;

        public Builder viewStyle(int viewStyle) {
            this.viewStyle = viewStyle;
            return this;
        }

        public Builder minYear(int minYear) {
            this.minYear = minYear;
            return this;
        }

        public Builder maxYear(int maxYear) {
            this.maxYear = maxYear;
            return this;
        }

        public Builder textCancel(String textCancel) {
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm) {
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder dateChose(String dateChose) {
            this.dateChose = dateChose;
            return this;
        }

        public Builder colorCancel(int colorCancel) {
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm) {
            this.colorConfirm = colorConfirm;
            return this;
        }

        /**
         * set btn text btnTextSize
         *
         * @param textSize dp
         */
        public Builder btnTextSize(int textSize) {
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize) {
            this.viewTextSize = textSize;
            return this;
        }

        public DatePickerPopWin build() {
            if (minYear > maxYear) {
                throw new IllegalArgumentException();
            }
            return new DatePickerPopWin(this);
        }
    }


    public DatePickerPopWin(Builder builder) {
        this.viewStyle = builder.viewStyle;
        this.minYear = builder.minYear;
        this.maxYear = builder.maxYear;
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        setSelectedDate(builder.dateChose);
        initView();
    }

    private OnDatePickedListener mListener;

    private void initView() {

        contentView = LayoutInflater.from(mContext).inflate(
                R.layout.layout_date_picker, null);

        //three wheel pick
        leftLoopView = (LoopView) contentView.findViewById(R.id.picker_left);
        middleLoopView = (LoopView) contentView.findViewById(R.id.picker_middle);
        rightLoopView = (LoopView) contentView.findViewById(R.id.picker_right);
        tv_pickerRight = (TextView) contentView.findViewById(R.id.picker_right_tv);
        pickerContainerV = contentView.findViewById(R.id.container_picker);

        switch (viewStyle) {
            case 1:
                break;
            case 2:
                leftLoopView.setVisibility(View.GONE);
                rightLoopView.setVisibility(View.GONE);
                tv_pickerRight.setVisibility(View.VISIBLE);
                tv_pickerRight.setText("cm");
                tv_pickerRight.setTextSize(viewTextSize);
                break;
            case 3:
                middleLoopView.setVisibility(View.GONE);
                rightLoopView.setVisibility(View.GONE);
                tv_pickerRight.setVisibility(View.VISIBLE);
                tv_pickerRight.setTextSize(viewTextSize);
                tv_pickerRight.setText("kg");
                break;
            case 4:
                leftLoopView.setVisibility(View.INVISIBLE);
                rightLoopView.setVisibility(View.INVISIBLE);
                tv_pickerRight.setVisibility(View.VISIBLE);
                tv_pickerRight.setTextSize(viewTextSize);
                tv_pickerRight.setText("minute");
                break;
            case 5:
                leftLoopView.setVisibility(View.INVISIBLE);
                rightLoopView.setVisibility(View.INVISIBLE);
                tv_pickerRight.setVisibility(View.VISIBLE);
                tv_pickerRight.setTextSize(viewTextSize);
                tv_pickerRight.setText("minute");
                break;
        }

        //do not loop,default can loop
        leftLoopView.setNotLoop();
        middleLoopView.setNotLoop();
        rightLoopView.setNotLoop();

        //set loopview text btnTextsize
        leftLoopView.setTextSize(viewTextSize);
        middleLoopView.setTextSize(viewTextSize);
        rightLoopView.setTextSize(viewTextSize);

        //set checked listen
        leftLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                leftPos = item;
                initDayPickerView();
            }
        });
        middleLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                middlePos = item;
                initDayPickerView();
            }
        });
        rightLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                rightPos = item;

            }
        });

        initPickerViews(); // init year and month loop view
        initDayPickerView(); //init day loop view

        contentView.setOnClickListener(this);

        setTouchable(true);
        setFocusable(true);
        // setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Init year and month loop view,
     * Let the day loop view be handled separately
     */
    private void initPickerViews() {

        switch (viewStyle) {
            case 1:
                int yearCount = maxYear - minYear;

                for (int i = 0; i <= yearCount; i++) {
                    leftList.add(format2LenStr(minYear + i));
                }

                for (int j = 0; j < 12; j++) {
                    middleList.add(format2LenStr(j + 1));
                }

                leftLoopView.setArrayList((ArrayList) leftList);
                leftLoopView.setInitPosition(leftPos);

                middleLoopView.setArrayList((ArrayList) middleList);
                middleLoopView.setInitPosition(middlePos);
                break;

            case 2://height
                for (int j = 120; j <= 300; j++) {
                    heightList.add(format2LenStr(j));

                }
                middleLoopView.setArrayList((ArrayList) heightList);
                middleLoopView.setInitPosition(middlePos);
                break;

            case 3://weight
                for (int i = 25; i <= 300; i++) {
                    weightList.add(format2LenStr(i));
                }
                for (int i = 0; i < 10; i++) {
                    weightListPoint.add("." + i);
                }
                leftLoopView.setArrayList((ArrayList) weightList);
                leftLoopView.setInitPosition(leftPos);
                middleLoopView.setArrayList((ArrayList) weightListPoint);
                middleLoopView.setInitPosition(middlePos);
                break;
            case 4:
                for (int i = 30; i <= 720; i += 30) {
                    heightList.add(format2LenStr(i));
                }
                middleLoopView.setArrayList((ArrayList) heightList);
                middleLoopView.setInitPosition(middlePos);
                break;
            case 5:
                for (int i = 30; i <= 240; i += 30) {
                    heightList.add(format2LenStr(i));
                }
                middleLoopView.setArrayList((ArrayList) heightList);
                middleLoopView.setInitPosition(middlePos);
                break;

        }

    }

    /**
     * Init day item
     */
    private void initDayPickerView() {

        int dayMaxInMonth;
        Calendar calendar = Calendar.getInstance();
        rightList = new ArrayList<String>();

        calendar.set(Calendar.YEAR, minYear + leftPos);
        calendar.set(Calendar.MONTH, middlePos);

        //get max day in month
        dayMaxInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < dayMaxInMonth; i++) {
            rightList.add(format2LenStr(i + 1));
        }

        rightLoopView.setArrayList((ArrayList) rightList);
        rightLoopView.setInitPosition(rightPos);
    }

    /**
     * set selected date position value when initView.
     *
     * @param dateStr
     */
    public void setSelectedDate(String dateStr) {
        if (!TextUtils.isEmpty(dateStr)) {
            long milliseconds = getLongFromyyyyMMdd(dateStr);
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            switch (viewStyle) {
                case 1:
                    if (milliseconds != -1) {
                        calendar.setTimeInMillis(milliseconds);
                        leftPos = calendar.get(Calendar.YEAR) - minYear;
                        middlePos = calendar.get(Calendar.MONTH);
                        rightPos = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                    }
                    break;
                case 2:
                    middlePos = new Integer(dateStr).intValue() - 120;
                    break;
                case 3:
                    leftPos = new Integer(dateStr).intValue() - 25;
                    break;
                case 4:
                    middlePos = new Integer(dateStr).intValue();
                    break;
                case 5:
                    middlePos = new Integer(dateStr).intValue();
                    break;
            }
        }
    }

    /**
     * Show date picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(200);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss date picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(300);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    @Override
    public void onClick(View v) {
        if (v == contentView) {
            dismissPopWin();
        }
        if (null != mListener) {
            switch (viewStyle) {
                case 1:
                    int year = minYear + leftPos;
                    int month = middlePos + 1;
                    int day = rightPos + 1;
                    StringBuffer sb = new StringBuffer();
                    sb.append(String.valueOf(year));
                    sb.append("-");
                    sb.append(format2LenStr(month));
                    sb.append("-");
                    sb.append(format2LenStr(day));
                    mListener.onDatePickCompleted(year, month, day, sb.toString());
                    break;
                case 2:
                    int height = middlePos;
                    mListener.onDatePickCompleted(0, height, 0, height + 120 + "");
                    break;
                case 3:
                    int weight = leftPos;
                    int porint = middlePos;
                    mListener.onDatePickCompleted(weight, porint, 0, (weight + 25) + "");
                    break;
                case 4:
                    int time = new Integer(heightList.get(middlePos)).intValue();
                    mListener.onDatePickCompleted(0, time, 0, time + "");
                    break;
                case 5:
                    int duration = new Integer(heightList.get(middlePos)).intValue();
                    mListener.onDatePickCompleted(0, duration, 0, duration + "");
                    break;
                default:
                    dismissPopWin();
                    break;
            }

        }
    }

    /**
     * get long from yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static long getLongFromyyyyMMdd(String date) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date parse = null;
        try {
            parse = mFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (parse != null) {
            return parse.getTime();
        } else {
            return -1;
        }
    }

    public static String getStrDate() {
        SimpleDateFormat dd = new SimpleDateFormat("MM-dd-yyyy", Locale.CHINA);
        return dd.format(new Date());
    }

    /**
     * Transform int to String with prefix "0" if less than 10
     *
     * @param num
     * @return
     */
    public static String format2LenStr(int num) {

        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    public static int spToPx(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public interface OnDatePickedListener {

        /**
         * Listener when date has been checked
         *
         * @param year
         * @param month
         * @param day
         * @param dateDesc yyyy-MM-dd
         */
        void onDatePickCompleted(int month, int day, int year,
                                 String dateDesc);

    }
}
