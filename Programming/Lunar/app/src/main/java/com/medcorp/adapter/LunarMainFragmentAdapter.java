package com.medcorp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.R;
import com.medcorp.fragment.MainClockFragment;
import com.medcorp.fragment.MainFragment;
import com.medcorp.fragment.MainSleepFragment;
import com.medcorp.fragment.MainSolarFragment;
import com.medcorp.fragment.MainStepsFragment;

/**
 * Created by Administrator on 2016/7/19.
 */
public class LunarMainFragmentAdapter extends FragmentPagerAdapter {

    private MainFragment mainFragment;
    private Context context;
    private String[] fragmentAdapterArray;

    public LunarMainFragmentAdapter(FragmentManager fm, MainFragment fragment) {
        super(fm);
        this.mainFragment = fragment;
        context = fragment.getContext();
        fragmentAdapterArray = context.getResources().getStringArray(R.array.lunar_main_adapter_fragment);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainClockFragment.instantiate(context, MainClockFragment.class.getName());
            case 1:
                return MainStepsFragment.instantiate(context, MainStepsFragment.class.getName());
            case 2:
                return MainSleepFragment.instantiate(context, MainSleepFragment.class.getName());
            case 3:
                return MainSolarFragment.instantiate(context, MainSolarFragment.class.getName());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragmentAdapterArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentAdapterArray[position];
    }
}
