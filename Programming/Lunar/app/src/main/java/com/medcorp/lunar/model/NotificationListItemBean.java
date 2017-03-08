package com.medcorp.lunar.model;

/**
 * Created by Jason on 2016/9/2.
 */
public class NotificationListItemBean {
    private int lampId;
    private String notificationTimeText;
    private boolean isChecked;

    public int getLampId() {
        return lampId;
    }

    public void setLampId(int lampId) {
        this.lampId = lampId;
    }

    public String getNotificationTimeText() {
        return notificationTimeText;
    }

    public void setNotificationTimeText(String notificationTimeText) {
        this.notificationTimeText = notificationTimeText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
