package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.adapter.LunarMainFragmentAdapter;
import com.medcorp.lunar.event.bluetooth.RequestResponseEvent;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.util.Common;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * Created by Administrator on 2016/7/19.
 */
public class MainFragment extends BaseObservableFragment {

    @Bind(R.id.main_fragment_title_tab_layout)
    TabLayout mainFragmentTitleTabLayout;
    @Bind(R.id.fragment_lunar_main_view_pager)
    ViewPager showWatchViewPage;
    @Bind(R.id.ui_page_control_point)
    LinearLayout uiPageControl;

    private boolean showSyncGoal;
    private LunarMainFragmentAdapter adapter;
    private String[] fragmentAdapterArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lunar_main_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initUiControl();
        mainFragmentTitleTabLayout.setupWithViewPager(showWatchViewPage);
        adapter = new LunarMainFragmentAdapter(getChildFragmentManager(), this);
        showWatchViewPage.setAdapter(adapter);
        return view;
    }

    private void initUiControl() {
        fragmentAdapterArray = getResources().getStringArray(R.array.lunar_main_adapter_fragment);
        uiPageControl.removeAllViews();
        for (int i = 0; i < fragmentAdapterArray.length; i++) {
            ImageView imageView = new ImageView(MainFragment.this.getContext());
            if (i == 0) {
                imageView.setImageResource(R.drawable.ui_page_control_selector);
            } else {
                imageView.setImageResource(R.drawable.ui_page_control_unselector);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                params.leftMargin = 20;
            }
            uiPageControl.addView(imageView, params);
        }

        showWatchViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    int childCount = uiPageControl.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        ImageView im = (ImageView) uiPageControl.getChildAt(i);
                        if (position == i) {
                            im.setImageResource(R.drawable.ui_page_control_selector);
                        } else {
                            im.setImageResource(R.drawable.ui_page_control_unselector);
                        }
                    }
                }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //NOTICE: if do full big sync, that will consume more battery power and more time (MAX 7 days data),so only big sync today's data
        if (Common.removeTimeFromDate(new Date()).getTime() == Common.removeTimeFromDate(new Date()).getTime()) {
            getModel().getSyncController().getDailyTrackerInfo(false);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(RequestResponseEvent event) {
        //if this response comes from syncController init, ignore it, only for user set a new goal.
        if (showSyncGoal) {
            showSyncGoal = false;
            int id = event.isSuccess() ? R.string.goal_synced : R.string.goal_error_sync;
            ((MainActivity) getActivity()).showStateString(id, false);
        }
    }

    public interface ObtainGoalListener {
        void obtainGoal(List<StepsGoal> list);
    }
}

