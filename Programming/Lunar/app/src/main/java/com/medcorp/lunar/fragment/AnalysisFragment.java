package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.AnalysisFragmentPagerAdapter;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * Created by Administrator on 2016/7/14.
 */
public class AnalysisFragment extends BaseObservableFragment{

    @Bind(R.id.analysis_fragment_indicator_tab)
    TabLayout analysisTable;
    @Bind(R.id.analysis_fragment_content_view_pager)
    ViewPager analysisViewpager;
    private AnalysisFragmentPagerAdapter mAnalysisAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analysis_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        mAnalysisAdapter = new AnalysisFragmentPagerAdapter(getChildFragmentManager(), this);
        analysisViewpager.setAdapter(mAnalysisAdapter);
        analysisTable.setupWithViewPager(analysisViewpager);
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
        menu.findItem(R.id.add_menu).setVisible(false);
    }
}



