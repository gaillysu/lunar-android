package com.medcorp.lunar.ble.model.packet;


import com.medcorp.lunar.model.DailyHistory;

import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.HexUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by med on 16/7/29.
 */
public class DailyTrackerInfoPacket extends Packet {
    public DailyTrackerInfoPacket(List<MEDRawData> packets) {
        super(packets);
    }

    /**
     * return Tracker history summary infomation, MAX total 7 days(include Today)
     * the actually days is saved by [DailyHistory].count
     */
    public List<DailyHistory> getDailyTrackerInfo(){

        List<DailyHistory> days = new ArrayList<DailyHistory>();

        int total = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[12]});
        int year = 0;
        int month = 0;
        int day = 0;

        for (int i = 0; i<total; i++)
        {
            if(i<=3)
            {
                year = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[2+4*i],getPackets().get(0).getRawData()[3+4*i]});
                month = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[4 + 4 * i]});
                day   = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[5 + 4 * i]});
            }
            else if(i == 4)
            {
                year = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[2+4*i],getPackets().get(0).getRawData()[3+4*i]});
                month = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[2]});
                day   = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[3]});
            }
            else if(i == 5)
            {
                year = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[4],getPackets().get(1).getRawData()[5]});
                month = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[6]});
                day   = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[7]});
            }
            else if(i == 6)
            {
                year = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[8],getPackets().get(1).getRawData()[9]});
                month = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[10]});
                day   = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[11]});
            }
            //vaild year,month, day
            if((year>1970 && year<2050)  && (month>=1 && month<=12) && (day>=1 && day<=31) )
            {
                //20150316
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date date = format.parse(String.format("%04d%02d%02d000000",year,month,day));
                    days.add(new DailyHistory(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        return days;

    }
}
