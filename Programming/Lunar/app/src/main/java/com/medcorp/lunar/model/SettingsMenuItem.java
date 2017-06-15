package com.medcorp.lunar.model;

/**
 * Created by gaillysu on 16/1/6.
 */
public class SettingsMenuItem {
    private String title;
    private String subtitle;
    private int icon;
    private boolean hasSwitch = false;
    private boolean switchStatus = false;

    public SettingsMenuItem(String title, int icon)
    {
        this.title = title;
        this.icon  = icon;
    }

    public SettingsMenuItem(String title,int icon, boolean switchStatus)
    {
        this.title = title;
        this.icon  = icon;
        hasSwitch = true;
        this.switchStatus = switchStatus;
    }

    public SettingsMenuItem(String title,String subtitle, int icon)
    {
        this.title = title;
        this.subtitle = subtitle;
        this.icon  = icon;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
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
