package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.lunar.R;
import com.medcorp.lunar.fragment.MainClockFragment;
import com.medcorp.lunar.fragment.MainFragment;
import com.medcorp.lunar.fragment.MainSolarDetailsFragment;


/***
 * Created by Administrator on 2016/7/19.
 */
public class LunarMainFragmentAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] fragmentAdapterArray;

    public LunarMainFragmentAdapter(FragmentManager fm, MainFragment fragment) {
        super(fm);
        context = fragment.getContext();
        fragmentAdapterArray = context.getResources().getStringArray(R.array.lunar_main_adapter_fragment);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainClockFragment.instantiate(context, MainClockFragment.class.getName());
            case 1:
                return MainSolarDetailsFragment.instantiate(context,MainSolarDetailsFragment.class.getName());
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
