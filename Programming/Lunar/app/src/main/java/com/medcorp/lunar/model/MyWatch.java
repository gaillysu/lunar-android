package com.medcorp.lunar.model;

import java.util.List;

/***
 * Created by gaillysu on 15/12/28.
 */
public class MyWatch {
    private String bleFirmwareVersion;
    private String mcuFirmwareVersion;
    private String appVersion;
    private int batteryLevel;
    private boolean availableVersion;
    private List<String> firmwareURLs;

    public MyWatch(String bleFirmwareVersion, String mcuFirmwareVersion, String appVersion, int batteryLevel, boolean availableVersion, List<String> firmwareURLs) {
        this.bleFirmwareVersion = bleFirmwareVersion;
        this.mcuFirmwareVersion = mcuFirmwareVersion;
        this.appVersion = appVersion;
        this.batteryLevel = batteryLevel;
        this.availableVersion = availableVersion;
        this.firmwareURLs = firmwareURLs;
    }

    public String getBleFirmwareVersion() {
        return bleFirmwareVersion;
    }

    public void setBleFirmwareVersion(String bleFirmwareVersion) {
        this.bleFirmwareVersion = bleFirmwareVersion;
    }

    public String getMcuFirmwareVersion() {
        return mcuFirmwareVersion;
    }

    public void setMcuFirmwareVersion(String mcuFirmwareVersion) {
        this.mcuFirmwareVersion = mcuFirmwareVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isAvailableVersion() {
        return availableVersion;
    }

    public void setAvailableVersion(boolean availableVersion) {
        this.availableVersion = availableVersion;
    }

    public List<String> getFirmwareURLs() {
        return firmwareURLs;
    }

    public void setFirmwareURLs(List<String> firmwareURLs) {
        this.firmwareURLs = firmwareURLs;
    }
}
