package com.medcorp.lunar.model;

/**
 * Created by gaillysu on 16/1/6.
 */
public class SettingsMenuItem {
    private String title;
    private int icon;
    private boolean hasSwitch = false;
    private boolean switchStatus = false;

    public SettingsMenuItem(String title, int icon)
    {
        this.title = title;
        this.icon  = icon;
    }

    public SettingsMenuItem(String title, int icon, boolean switchStatus)
    {
        this.title = title;
        this.icon  = icon;
        hasSwitch = true;
        this.switchStatus = switchStatus;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isWithSwitch() {
        return hasSwitch;
    }

    public boolean isSwitchOn() {
        return switchStatus;
    }

    public void setSwitchStatus(boolean switchStatus) {
        this.switchStatus = switchStatus;
    }
}
