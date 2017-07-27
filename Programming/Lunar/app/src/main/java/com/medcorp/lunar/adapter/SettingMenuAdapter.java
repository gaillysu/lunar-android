package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.listener.OnCheckedChangeInListListener;
import com.medcorp.lunar.model.SettingsMenuItem;
import com.medcorp.lunar.view.ToastHelper;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by gaillysu on 16/1/6.
 */
public class SettingMenuAdapter extends ArrayAdapter<SettingsMenuItem> {
    private Context context;
    private List<SettingsMenuItem> listMenu;
    private OnCheckedChangeInListListener onCheckedChangeInListListener;
    private List<SwitchCompat> switchCompatList;

    public SettingMenuAdapter(Context context, List<SettingsMenuItem> listMenu, OnCheckedChangeInListListener listener) {
        super(context, 0, listMenu);
        this.context = context;
        this.listMenu = listMenu;
        this.onCheckedChangeInListListener = listener;
        switchCompatList = new ArrayList<>();
    }

    public SettingMenuAdapter(Context context, List<SettingsMenuItem> listMenu) {
        this(context, listMenu, null);
    }

    @Override
    public int getCount() {
        return listMenu == null ? 0 : listMenu.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_setting_menu_list_view_item, parent, false);
        ImageView menuImage = (ImageView) itemView.findViewById(R.id.activity_setting_menu_image);
        RobotoTextView menuNameTextView = (RobotoTextView) itemView.findViewById(R.id.activity_setting_menu_name);
        final SwitchCompat onOffSwitch = (SwitchCompat) itemView.findViewById(R.id.activity_setting_menu_switch);
        RobotoTextView subtitle = (RobotoTextView) itemView.findViewById(R.id.activity_setting_menu_subtitle);
        menuImage.setImageResource(listMenu.get(position).getIcon());
        menuNameTextView.setText(listMenu.get(position).getTitle());
        if (listMenu.get(position).getSubtitle() != null) {
            subtitle.setText(listMenu.get(position).getSubtitle());
            subtitle.setVisibility(View.VISIBLE);
        }
        if (listMenu.get(position).isWithSwitch()) {
            switchCompatList.add(onOffSwitch);
            onOffSwitch.setVisibility(View.VISIBLE);
            onOffSwitch.setChecked(listMenu.get(position).isSwitchOn());
            final boolean checked = onOffSwitch.isChecked();
            onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (ApplicationModel.getInstance().isWatchConnected()) {
                        onCheckedChangeInListListener.onCheckedChange(buttonView, isChecked, position);
                    } else {
                        onOffSwitch.setChecked(checked);
                        ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
                    }
                }
            });
        } else {
            onOffSwitch.setVisibility(View.INVISIBLE);
        }
        return itemView;
    }

    public void toggleSwitch(int i, boolean status) {
        if (i < switchCompatList.size()) {
            switchCompatList.get(i).setChecked(status);
        }
    }
}
