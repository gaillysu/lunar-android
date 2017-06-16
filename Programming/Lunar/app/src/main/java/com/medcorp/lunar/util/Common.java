package com.medcorp.lunar.util;

import android.content.Context;

import com.medcorp.lunar.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public static Date removeTimeFromDate(Date date) {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        return calBeginning.getTime();
    }

    public static String getUTCTimestampFromLocalDate(Date localDate) {
        Date localMidnight = removeTimeFromDate(localDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(localMidnight);
    }

    public static Date getLocalDateFromUTCTimestamp(String timestamp,String utc_offset) {
        Date date = new Date();
        String[] offsetArray = utc_offset.split(":");
        long offset = Integer.parseInt(offsetArray[0].substring(1)) * 60 *60 *1000l;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = sdf.parse(timestamp);
            if(offsetArray[0].startsWith("+")) {
                date = new Date(sdf.parse(timestamp).getTime() + offset);
            } else {
                date = new Date(sdf.parse(timestamp).getTime() - offset);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    static public int getBuildInSoftwareVersion(Context context, int watchID) {
        int buildinSoftwareVersion = 0;
        String[]files;
        String firmwarePath = NEVO_FIRMWARE_PATH;
        if(watchID == 2){
            firmwarePath = NEVO_SOLAR_FIRMWARE_PATH;
        }
        try {
            files = context.getAssets().list(firmwarePath);
            for(String file:files) {
                if(file.contains(".bin")) {
                    int start  = file.toLowerCase().indexOf("_v");
                    int end = file.toLowerCase().indexOf(".bin");
                    String vString = file.substring(start+2,end);
                    if(start != -1 && vString != null) {
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

    static public List<String> getAllBuildInZipFirmwareURLs(Context context, int watchID)
    {
        ArrayList<String> buildinZipFirmware = new ArrayList<>();
        if(watchID == 3)
        {
            buildinZipFirmware.add(context.getResources().getString(R.string.lunar_firmware));
        }
        return  buildinZipFirmware;
    }

    static public int getBuildInZipFirmwareRawResID(Context context, int watchID)
    {
        if(watchID == 3)
        {
            //NOTICE: don't forget fixing firmwares.xml
            return  R.raw.lunar_20170606_v15;
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
