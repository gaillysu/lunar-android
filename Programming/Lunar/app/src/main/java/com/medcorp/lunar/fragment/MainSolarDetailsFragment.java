package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.lunar.R;
import com.medcorp.lunar.fragment.base.BaseFragment;

import butterknife.ButterKnife;

/***
 * Created by Jason on 2017/6/22.
 */

public class MainSolarDetailsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_solar_details_fragment,container,false);
        ButterKnife.bind(this,view);
        initData();
        return view;
    }

    private void initData() {

    }
}
