package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.AnalysisFragmentPagerAdapter;
import com.medcorp.lunar.event.bluetooth.OnSyncEvent;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.model.ChangeFragmentPageModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/14.
 */
public class AnalysisFragment extends BaseObservableFragment implements ViewPager.OnPageChangeListener {

    @Bind(R.id.analysis_fragment_indicator_tab)
    TabLayout analysisTable;
    @Bind(R.id.analysis_fragment_content_view_pager)
    ViewPager analysisViewpager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.analysis_fragment_layout,container,false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        AnalysisFragmentPagerAdapter adapter = new AnalysisFragmentPagerAdapter(getChildFragmentManager(),this);
        analysisViewpager.setAdapter(adapter);
        analysisTable.setupWithViewPager(analysisViewpager);
        analysisViewpager.addOnPageChangeListener(this);
        return view;
    }

    @Subscribe
    public void onEvent(OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int currentItem = analysisViewpager.getCurrentItem();
                    AnalysisFragmentPagerAdapter adapter = new AnalysisFragmentPagerAdapter(getChildFragmentManager(), AnalysisFragment.this);
                    analysisViewpager.setAdapter(adapter);
                    analysisTable.setupWithViewPager(analysisViewpager);
                    analysisViewpager.setCurrentItem(currentItem);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
        menu.findItem(R.id.add_menu).setVisible(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        EventBus.getDefault().post(new ChangeFragmentPageModel(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}



