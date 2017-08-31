package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.lunar.R;
import com.medcorp.lunar.fragment.AnalysisFragment;
import com.medcorp.lunar.fragment.AnalysisSleepFragment;
import com.medcorp.lunar.fragment.AnalysisSolarFragment;
import com.medcorp.lunar.fragment.AnalysisStepsFragment;


/**
 * Created by Administrator on 2016/7/21.
 */
public class AnalysisFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] analysisTableArray;
    private AnalysisFragment analysisFragment;

    public AnalysisFragmentPagerAdapter(FragmentManager fm, AnalysisFragment fragment) {
        super(fm);
        this.analysisFragment = fragment;
        context = fragment.getContext();
        analysisTableArray = fragment.getResources().getStringArray(R.array.analysis_fragment_table_array);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AnalysisStepsFragment.instantiate(context, AnalysisStepsFragment.class.getName());
            case 1:
                return  AnalysisSleepFragment.instantiate(context, AnalysisSleepFragment.class.getName());
            case 2:
                return AnalysisSolarFragment.instantiate(context, AnalysisSolarFragment.class.getName());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return analysisTableArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return analysisTableArray[position];
    }
}
