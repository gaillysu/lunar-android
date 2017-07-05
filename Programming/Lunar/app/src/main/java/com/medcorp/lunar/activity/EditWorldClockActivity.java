package com.medcorp.lunar.activity;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.ChooseCityAdapter;
import com.medcorp.lunar.adapter.SearchWorldAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.bluetooth.PositionAddressChangeEvent;
import com.medcorp.lunar.model.ChooseCityViewModel;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.PinyinComparator;
import com.medcorp.lunar.view.SideBar;

import net.medcorp.library.worldclock.City;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason on 2016/10/26.
 */

public class EditWorldClockActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.show_all_city_list)
    ListView showAllCityList;
    @Bind(R.id.choose_activity_list_index_sidebar)
    SideBar sortCityBar;
    @Bind(R.id.search_world_city_edit_city_name_ed)
    EditText searchCityAutoCompleteTv;
    @Bind(R.id.search_city_result_list)
    ListView searchResultListView;
    @Bind(R.id.edit_home_city_tv)
    TextView positionCityName;

    private List<City> cities;
    private List<ChooseCityViewModel> chooseCityViewModelsList;
    private PinyinComparator pinyinComparator;
    private ChooseCityAdapter allCityAdapter;
    private SearchWorldAdapter autoAdapter;
    private List<ChooseCityViewModel> resultList;
    private String cityName;
    private String countryName;
    private Address mPositionLocal;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_clock_activity_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.choose_activity_title_choose_city_tv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }


    private void initData() {
        resultList = new ArrayList<>();
        autoAdapter = new SearchWorldAdapter(resultList, this);
        searchResultListView.setAdapter(autoAdapter);
        cities = getModel().getWorldClockDatabaseHelper().getAll();
        mPositionLocal = Preferences.getLocation(this);
        if (mPositionLocal != null) {
            cityName = mPositionLocal.getLocality();
            countryName = mPositionLocal.getCountryName();
        } else {
            TimeZone timeZone = Calendar.getInstance().getTimeZone();
            String localCityName = timeZone.getID().split("/")[1].replace("_", " ");
            City city = getModel().getWorldClockDatabaseHelper().get(localCityName);
            cityName = city.getName();
            countryName = city.getCountry();
        }

        positionCityName.setText(cityName + "," + countryName);

        chooseCityViewModelsList = new ArrayList<>();
        pinyinComparator = new PinyinComparator();

        for (int i = 0; i < cities.size(); i++) {
            chooseCityViewModelsList.add(new ChooseCityViewModel(cities.get(i)));
        }
        //position City click event
        positionCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.savePositionCity(EditWorldClockActivity.this, cityName);
                Preferences.savePositionCountry(EditWorldClockActivity.this, countryName);
                Preferences.saveHomeCityCalender(EditWorldClockActivity.this, Calendar.getInstance().getTimeZone().getID());
                searchCityAutoCompleteTv.setText(cityName + ", " + countryName);
                finish();
            }
        });

        allCityAdapter = new ChooseCityAdapter(this, chooseCityViewModelsList,false);
        showAllCityList.setAdapter(allCityAdapter);
        sortCityBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = allCityAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    showAllCityList.setSelection(position);
                }
            }
        });

        showAllCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity(chooseCityViewModelsList.get(position).getCityId());
            }
        });

        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity(resultList.get(position).getCityId());
            }
        });
        searchCityAutoCompleteTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode()
                        == KeyEvent.KEYCODE_ENTER)) {
                    resultList.clear();
                    return true;
                }
                return false;
            }
        });

        searchCityAutoCompleteTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence content, int start, int before, int count) {
                resultList.clear();
                searchCity(content.toString());
                autoAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(PositionAddressChangeEvent addressDateEvent) {
        mPositionLocal = addressDateEvent.getAddress();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                positionCityName.setText(mPositionLocal.getLocality() + "," + mPositionLocal.getCountryName());
            }
        });
    }

    @OnClick(R.id.edit_world_clock_cancel_search_button)
    public void cancelClick() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchCity(String content) {
        if (!TextUtils.isEmpty(content)) {
            searchResultListView.setVisibility(View.VISIBLE);
            for (ChooseCityViewModel chooseCityModel : chooseCityViewModelsList) {
                if (chooseCityModel.getDisplayName().toLowerCase().contains(content.toLowerCase())) {
                    resultList.add(chooseCityModel);
                }
            }
            if (resultList.size() > 0) {
                Collections.sort(resultList, pinyinComparator);
                autoAdapter.notifyDataSetChanged();
            }
        }
    }

    //save select city
    public void selectCity(int cityId) {
        //保存home time
        City selectCity = getModel().getWorldClockDatabaseHelper().get(cityId);
        String temp = selectCity.getTimezoneRef().getGmt();
        temp = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
        Preferences.savePositionCity(EditWorldClockActivity.this, selectCity.getName());
        Preferences.savePositionCountry(EditWorldClockActivity.this, selectCity.getCountry());
        Preferences.saveHomeCityCalender(EditWorldClockActivity.this, temp);

        searchCityAutoCompleteTv.setText(selectCity.getName());
        allCityAdapter.notifyDataSetChanged();
        finish();
    }
}