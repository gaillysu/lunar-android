package com.medcorp.util;

import android.content.Context;

import com.medcorp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by gaillysu on 15/12/8.
 */
public class Common {

    //NOTICE,DON'T CHANGE THEIR VALUES, THEY COME FROM "src/nevo/assets/firmware","src/nevo/assets/solar_firmware"
    private static final String NEVO_FIRMWARE_PATH = "firmware";
    private static final String NEVO_SOLAR_FIRMWARE_PATH = "solar_firmware";
    /**
     * return one day which start 00:00:00
     * @param date : YYYY/MM/DD HH:MM:SS
     * @return : YYYY/MM/DD 00:00:00
     */
    public static Date removeTimeFromDate(Date date)
    {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date today = calBeginning.getTime();
        return today;
    }

    public static String getUTCTimestampFromLocalDate(Date localDate)
    {
        Date localMidnight = removeTimeFromDate(localDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp  = sdf.format(localMidnight);
        return timestamp;
    }

    public static Date getLocalDateFromUTCTimestamp(String timestamp,String utc_offset)
    {
        Date date = new Date();
        String[] offsetArray = utc_offset.split(":");
        long offset = Integer.parseInt(offsetArray[0].substring(1)) * 60 *60 *1000l;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = sdf.parse(timestamp);
            if(offsetArray[0].startsWith("+"))
            {
                date = new Date(sdf.parse(timestamp).getTime() + offset);
            }
            else
            {
                date = new Date(sdf.parse(timestamp).getTime() - offset);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     *
     * @param context
     * @return build in MCU firmawre version
     */
    static public int getBuildinSoftwareVersion(Context context,int watchID)
    {
        int buildinSoftwareVersion = 0;
        String[]files;
        String firmwarePath = NEVO_FIRMWARE_PATH;
        if(watchID == 2){
            firmwarePath = NEVO_SOLAR_FIRMWARE_PATH;
        }
        try {
            files = context.getAssets().list(firmwarePath);
            for(String file:files)
            {
                if(file.contains(".bin"))
                {
                    int start  = file.toLowerCase().indexOf("_v");
                    int end = file.toLowerCase().indexOf(".bin");
                    String vString = file.substring(start+2,end);
                    if(start!=-1&&vString != null)
                    {
                        buildinSoftwareVersion = Integer.parseInt(vString);
                        break;
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return buildinSoftwareVersion;
    }

    /**
     *
     * @param context
     * @return build-in BLE firmware version
     */
    static public int getBuildinFirmwareVersion(Context context,int watchID)
    {
        int buildinFirmwareVersion = 0;
        String[]files;
        String firmwarePath = NEVO_FIRMWARE_PATH;
        if(watchID == 2){
            firmwarePath = NEVO_SOLAR_FIRMWARE_PATH;
        }
        try {
            files = context.getAssets().list(firmwarePath);
            for(String file:files)
            {
                if(file.contains(".hex"))
                {
                    int start  = file.toLowerCase().indexOf("_v");
                    int end = file.toLowerCase().indexOf(".hex");
                    String vString = file.substring(start+2,end);
                    if(start!=-1&&vString != null)
                    {
                        buildinFirmwareVersion = Integer.parseInt(vString);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return buildinFirmwareVersion;
    }

    /**
     *
     * @param context
     * @return build-in ZIP firmware version
     */
    static public int getBuildinZipFirmwareVersion(Context context,String zipFileName)
    {
        int buildinFirmwareVersion = -1;
        int start  = zipFileName.toLowerCase().indexOf("_v");
        int end = zipFileName.toLowerCase().indexOf(".zip");
        String vString = zipFileName.substring(start+2,end);
        if(start!=-1&&vString != null)
        {
            buildinFirmwareVersion = Integer.parseInt(vString);
        }
        return buildinFirmwareVersion;
    }
    /**
     *
     * @param context
     * @param currentMcuVersion
     * @param currentBleVersion
     * @return need do OTA 's firmwares
     */
    static public List<String> needOTAFirmwareURLs(Context context,int currentMcuVersion, int currentBleVersion,int watchID)
    {
        List<String> firmwareURLs = new ArrayList<String>();
        String firmwarePath = NEVO_FIRMWARE_PATH;
        if(watchID == 2){
            firmwarePath = NEVO_SOLAR_FIRMWARE_PATH;
        }
        String[]files;
        int buildinSoftwareVersion = getBuildinSoftwareVersion(context,watchID);
        int buildinFirmwareVersion = getBuildinFirmwareVersion(context,watchID);

        try {
            files = context.getAssets().list(firmwarePath);
            for(String file:files)
            {
                if(file.contains(".hex"))
                {
                    if(currentBleVersion < buildinFirmwareVersion )
                    {
                        firmwareURLs.add(firmwarePath + "/" + file);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            files = context.getAssets().list(firmwarePath);
            for(String file:files)
            {
                if(file.contains(".bin"))
                {
                    if(currentMcuVersion < buildinSoftwareVersion )
                    {
                        //if MCU got broken and reinstall battery, firstly update MCU
                        if(currentMcuVersion == 0)
                            firmwareURLs.add(0,firmwarePath + "/" + file);
                        else
                            firmwareURLs.add(firmwarePath + "/" + file);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return firmwareURLs;
    }

    /**
     *
     * @param context
     * @return all build-in firmware, first is BLE, then MCU
     */
    static public List<String> getAllBuildinFirmwareURLs(Context context,int watchID)
    {
        return  needOTAFirmwareURLs(context,-1,-1,watchID);
    }

    /**
     *
     * @param context
     * @return all build-in ZIP firmware
     */
    static public List<String> getAllBuildinZipFirmwareURLs(Context context,int watchID)
    {
        ArrayList<String> buildinZipFirmware = new ArrayList<>();
        if(watchID == 3)
        {
            buildinZipFirmware.add(context.getResources().getString(R.string.lunar_firmware));
        }
        return  buildinZipFirmware;
    }
    /**
     *
     * @param context
     * @return all build-in ZIP firmware resource Raw ID
     */
    static public int getBuildinZipFirmwareRawResID(Context context,int watchID)
    {
        if(watchID == 3)
        {
            //NOTICE: don't forget fixing firmwares.xml
            return  R.raw.lunar_20170112_v7;
        }
        return  0;
    }

    public static int[] convertJSONArrayIntToArray(String string){
        try {
            JSONArray jsonArray = new JSONArray(string);
            int[] hourlyLight = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
                hourlyLight[i] = jsonArray.optInt(i,0);
            }
            return hourlyLight;
        } catch (JSONException e) {
            e.printStackTrace();
            return new int[0];
        }
    }

    /**
     * Mac add 1
     * @param macAddress Mac address，eg: AB:CD:EF:56:BF:D0
     * @return macAddress + 1,eg: AB:CD:EF:56:BF:D1
     */
    public static String getMacAdd(String macAddress) {
        String hexMacAddress = macAddress.toUpperCase().replaceAll(":","");
        String newHexMacAddress = Long.toHexString(Long.parseLong(hexMacAddress, 16) + 1).toUpperCase();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<12;i++) {
            if(i==2||i==4||i==6||i==8||i==10){
                stringBuilder.append(":");
            }
            stringBuilder.append(newHexMacAddress.substring(i,i+1));
        }
        return stringBuilder.toString();
    }
}
