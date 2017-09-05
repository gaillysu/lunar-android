package com.medcorp.lunar.activity.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.ChooseCityAdapter;
import com.medcorp.lunar.adapter.SearchWorldAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.ChooseCityViewModel;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.PinyinComparator;

import net.medcorp.library.worldclock.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * Created by Jason on 2017/6/29.
 */

public class SelectLocalCityActivity extends BaseActivity {

    @Bind(R.id.search_city_edit_city_name_ed)
    EditText searchCityNameEd;
    @Bind(R.id.search_city_result_list)
    ListView searchResultListView;
    @Bind(R.id.show_all_city_list)
    FastScrollRecyclerView showAllCityList;

    private ChooseCityAdapter allCityAdapter;
    private SearchWorldAdapter searchResultAdapter;
    private PinyinComparator pinyinComparator;
    private List<ChooseCityViewModel> searchResultList;
    private List<City> allCites;
    private List<ChooseCityViewModel> allCitesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_local_city_activity_view);
        ButterKnife.bind(this);
        pinyinComparator = new PinyinComparator();
        initData();
        initView();
    }

    private void initData() {
        searchResultList = new ArrayList<>();
        allCitesList = new ArrayList<>();
        allCites = getModel().getWorldClockDatabaseHelper().getAll();
        for (int i = 0; i < allCites.size(); i++) {
            allCitesList.add(new ChooseCityViewModel(allCites.get(i)));
        }
    }

    private void initView() {
        allCityAdapter = new ChooseCityAdapter(this, allCitesList, true);
        showAllCityList.setLayoutManager(new LinearLayoutManager(this));
        showAllCityList.setAdapter(allCityAdapter);
        searchResultAdapter = new SearchWorldAdapter(searchResultList, this);
        searchResultListView.setAdapter(searchResultAdapter);
        allCityAdapter.setRecyclerViewItemClick(new ChooseCityAdapter.RecyclerViewItemClick() {
            @Override
            public void onRecyclerViewItemClick(ChooseCityViewModel data) {

                saveLocalCity(data.getCityId());
                back();
            }
        });

        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    saveLocalCity(searchResultList.get(position).getCityId());
                    back();
            }
        });

        searchCityNameEd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode()
                        == KeyEvent.KEYCODE_ENTER)) {
                    searchResultList.clear();
                    return true;
                }
                return false;
            }
        });

        searchCityNameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence content, int start, int before, int count) {
                searchResultList.clear();
                searchCity(content.toString());
                searchResultAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void saveLocalCity(int cityId) {
        City selectCity = getModel().getWorldClockDatabaseHelper().get(cityId);
        String temp = selectCity.getTimezoneRef().getGmt();
        temp = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
        Preferences.savePositionCity(this, selectCity.getName());
        Preferences.savePositionCountry(this, selectCity.getCountry());
        Preferences.saveHomeCityCalender(this, temp);
        back();
    }

    private void searchCity(String content) {
        if (!TextUtils.isEmpty(content)) {
            searchResultListView.setVisibility(View.VISIBLE);
            for (ChooseCityViewModel chooseCityModel : allCitesList) {
                if (chooseCityModel.getDisplayName().toLowerCase().contains(content.toLowerCase())) {
                    searchResultList.add(chooseCityModel);
                }
            }
            if (searchResultList.size() > 0) {
                Collections.sort(searchResultList, pinyinComparator);
                searchResultAdapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.cancel_search_city)
    public void cancelSearch() {
        back();
    }

    private void back() {
        setResult(0x02);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
