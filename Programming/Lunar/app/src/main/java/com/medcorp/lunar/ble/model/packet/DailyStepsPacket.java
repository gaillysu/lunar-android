package com.medcorp.lunar.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.HexUtils;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by med on 16/8/1.
 */
public class DailyStepsPacket extends Packet {

    public DailyStepsPacket(List<MEDRawData> packets) {
        super(packets);
    }

    /**
     return the Current Daily steps
     */
    public int getDailySteps()
    {
        int dailySteps = 0;

        dailySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[2],
                getPackets().get(0).getRawData()[3],
                getPackets().get(0).getRawData()[4],
                getPackets().get(0).getRawData()[5]});
        return dailySteps;
    }
    /**
     return the Daily steps StepsGoal
     */
    public int getDailyStepsGoal()
    {
        int dailyStepGoal = 0;

        dailyStepGoal  = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[6],
                getPackets().get(0).getRawData()[7],
                getPackets().get(0).getRawData()[8],
                getPackets().get(0).getRawData()[9]});

        return dailyStepGoal;
    }

    /**
     return the Daily date, if setRTC is invoked correctly, it should be return current date & time
     */
    public Date getDailyDate()
    {
        int year = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[10],getPackets().get(0).getRawData()[11]});
        int month = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[12]});
        int day   = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[13]});
        int hour = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[14]});
        int minute   = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[15]});
        int second = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[16]});
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date date = format.parse(String.format("%04d%02d%02d%02d%02d%02d",year,month,day,hour,minute,second));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new DateTime(1970,1,1,0,0,0).toDate();
    }

}
