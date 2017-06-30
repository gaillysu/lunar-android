package com.medcorp.lunar.activity.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    ListView showAllCityList;
    @Bind(R.id.index_slide_dialog)
    TextView firstShowCity;

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
        showAllCityList.setAdapter(allCityAdapter);
        searchResultAdapter = new SearchWorldAdapter(searchResultList, this);
        searchResultListView.setAdapter(searchResultAdapter);
        showAllCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                saveLocalCity(allCitesList.get(position).getCityId());
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

        showAllCityList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstShowCity.setText(allCitesList.get(showAllCityList.getFirstVisiblePosition()).getSortLetter());
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
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
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
