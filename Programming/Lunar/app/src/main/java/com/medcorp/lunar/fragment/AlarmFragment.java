package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.EditAlarmActivity;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.adapter.AlarmArrayAdapter;
import com.medcorp.lunar.adapter.BedtimeAdapter;
import com.medcorp.lunar.ble.controller.SyncControllerImpl;
import com.medcorp.lunar.event.bluetooth.RequestResponseEvent;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.fragment.listener.OnAlarmSwitchListener;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/***
 * Created by karl-john on 11/12/15.
 */
public class AlarmFragment extends BaseObservableFragment
        implements OnAlarmSwitchListener, BedtimeAdapter.OnBedtimeSwitchListener, BedtimeAdapter.OnBedtimeDeleteListener {

    //    @Bind(R.id.fragment_alarm_list_view)
    //    ListView alarmListView;
    //    @Bind(R.id.fragment_alarm_list_view_bedtime)
    //    ListView bedtimeListView;
    //    @Bind(R.id.show_bedtime_alarm_view)
    //    LinearLayout bedtime;
    //    @Bind(R.id.show_normal_alarm_view)
    //    LinearLayout normalAlarm;
    @Bind(R.id.all_alarm_recycler_view)
    RecyclerView allAlarm;
    private List<Alarm> alarmList;
    private BedtimeAdapter bedtimeAdapter;
    private List<BedtimeModel> allBedtimeModels;
    private AlarmArrayAdapter normalAlarmAdapter;
    private boolean showSyncAlarm = false;
    private Alarm editAlarm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        alarmList = new ArrayList<>();
        allBedtimeModels = new ArrayList<>();
        initData();
        return view;
    }

    private void initData() {
        getModel().getAllAlarm(new SyncControllerImpl.SyncAlarmToWatchListener() {
            @Override
            public void syncAlarmToWatch(List<Alarm> alarms) {
                for (Alarm alarm : alarms) {
                    if (alarm.getAlarmNumber() > 6 && alarm.getAlarmNumber() < 13) {
                        alarmList.add(alarm);
                    }
                }

                getModel().getBedTimeDatabaseHelper().getAll().subscribe(new Consumer<List<BedtimeModel>>() {
                    @Override
                    public void accept(List<BedtimeModel> bedtimeModels) throws Exception {
                        allBedtimeModels.clear();
                        allBedtimeModels.addAll(bedtimeModels);
                        allAlarm.setLayoutManager(new LinearLayoutManager(AlarmFragment.this.getContext()));
                        bedtimeAdapter = new BedtimeAdapter(AlarmFragment.this, AlarmFragment.this,
                                getModel(), AlarmFragment.this.getContext(), alarmList, bedtimeModels);
                        allAlarm.setAdapter(bedtimeAdapter);
                    }
                });

            }
        });

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }

    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        switch (item.getItemId()) {
    //            case R.id.add_menu:
    //                if (!getModel().isWatchConnected()) {
    //                    ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
    //                    return false;
    //                }
    //                View selectType = LayoutInflater.from(AlarmFragment.this.getActivity()).inflate(R.layout.select_alarm_type_layout, null);
    //                final Dialog dialog = new AlertDialog.Builder(AlarmFragment.this.getActivity()).create();
    //                Button bedtime = (Button) selectType.findViewById(R.id.bedtime_bt);
    //                Button normalAlarm = (Button) selectType.findViewById(R.id.normal_alarm_bt);
    //                dialog.show();
    //                Window window = dialog.getWindow();
    //                window.setContentView(selectType);
    //
    //                bedtime.setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View v) {
    //                        dialog.dismiss();
    //                        Intent intent = new Intent(AlarmFragment.this.getActivity(),
    //                                EditNewBedtimeActivity.class);
    //                        startActivityForResult(intent, 0x02);
    //                    }
    //                });
    //
    //                normalAlarm.setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View v) {
    //                        dialog.dismiss();
    //                        Intent intent = new Intent(AlarmFragment.this.getActivity(),
    //                                EditNewAlarmActivity.class);
    //                        startActivityForResult(intent, 0x01);
    //                    }
    //                });
    //                break;
    //        }
    //        return super.onOptionsItemSelected(item);
    //    }

    @Override
    public void onAlarmSwitch(SwitchCompat alarmSwitch, Alarm alarm) {
        if (!getModel().isWatchConnected()) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        alarmSwitch.setChecked(alarmSwitch.isChecked());
        //save weekday to low 4 bit,bit 7 to save enable or disable
        alarm.setEnable(alarmSwitch.isChecked());
        Log.e("jason", alarmSwitch.isChecked() + "AAl");
        getModel().updateAlarm(alarm);
        showSyncAlarm = true;
        getModel().getSyncController().setAlarm(alarm);
        ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
    }

    private void syncAlarmByEditor(boolean delete) {
        if (!getModel().isWatchConnected()) {
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        if (delete) {
            showSyncAlarm = true;
            ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
        } else {
            getModel().getAlarmById(editAlarm.getId(), new EditAlarmActivity.ObtainAlarmListener() {
                @Override
                public void obtainAlarm(Alarm alarm) {
                    editAlarm = alarm;
                    showSyncAlarm = true;
                    getModel().getSyncController().setAlarm(editAlarm);
                    ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
                }
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(RequestResponseEvent event) {
        if (showSyncAlarm) {
            showSyncAlarm = false;
            int id = event.isSuccess() ? R.string.alarm_synced : R.string.alarm_error_sync;
            ((MainActivity) getActivity()).showStateString(id, false);
        }
    }

    @Override
    public void onBedtimeSwitch(final SwitchCompat alarmSwitch, BedtimeModel bedtime) {
        if (!getModel().isWatchConnected()) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        final byte[] alarmNumber = bedtime.getAlarmNumber();
        final boolean checked = alarmSwitch.isChecked();
        alarmSwitch.setChecked(checked);
        bedtime.setEnable(checked);
        getModel().getBedTimeDatabaseHelper().update(bedtime).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.i("jason", "success bedtime");
            }
        });
        for (int i = 0; i < alarmNumber.length; i++) {
            getModel().getAlarmDatabaseHelper().obtainAlarm(alarmNumber[i]).subscribe(new Consumer<Alarm>() {
                @Override
                public void accept(Alarm alarm) throws Exception {
                    alarm.setEnable(checked);
                    getModel().getSyncController().setAlarm(alarm);
                }
            });
            getModel().getAlarmDatabaseHelper().obtainAlarm(alarmNumber[i] + 13).subscribe(new Consumer<Alarm>() {
                @Override
                public void accept(Alarm alarm) throws Exception {
                    alarm.setEnable(checked);
                    getModel().getSyncController().setAlarm(alarm);
                }
            });
        }
        ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
    }


    @Override
    public void onBedtimeDelete(byte[] alarmNumber) {
        //TODO delete alarm

    }
}