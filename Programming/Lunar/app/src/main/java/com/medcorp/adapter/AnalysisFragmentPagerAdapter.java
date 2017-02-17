package com.medcorp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.R;
import com.medcorp.fragment.AnalysisFragment;
import com.medcorp.fragment.AnalysisSleepFragment;
import com.medcorp.fragment.AnalysisSolarFragment;
import com.medcorp.fragment.AnalysisStepsFragment;

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
        int watchID = analysisFragment.getModel().getSyncController().getWatchInfomation().getWatchID();
        if(watchID==1|| watchID == 0) {
            analysisTableArray = fragment.getResources().getStringArray(R.array.nevo_analysis_fragment_table_array);
        }else if(watchID == 2 || watchID ==3 ){
            analysisTableArray = fragment.getResources().getStringArray(R.array.analysis_fragment_table_array);
        }else{
            analysisTableArray = fragment.getResources().getStringArray(R.array.nevo_analysis_fragment_table_array);
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AnalysisStepsFragment analysisStepsFragment = (AnalysisStepsFragment) AnalysisStepsFragment.instantiate(context, AnalysisStepsFragment.class.getName());
                return analysisStepsFragment;
            case 1:
                AnalysisSleepFragment analysisSleepFragment = (AnalysisSleepFragment) AnalysisSleepFragment.instantiate(context, AnalysisSleepFragment.class.getName());
                return analysisSleepFragment;
            case 2:
                AnalysisSolarFragment analysisSolarFragment = (AnalysisSolarFragment) AnalysisSolarFragment.instantiate(context, AnalysisSolarFragment.class.getName());
                return analysisSolarFragment;
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
