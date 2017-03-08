package com.medcorp.lunar.ble.model.packet;


import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.HexUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by med on 16/7/29.
 */
public class DailyTrackerPacket extends Packet {
    final int HEADERLENGTH = 5;
    final int HOURLYPACKETSNUMBER = 2;

    public DailyTrackerPacket(List<MEDRawData> packets) {
        super(packets);
    }

    /**
     return which date, default is today
     */
    public Date getDate()
    {
        int year = 0;
        int month = 0;
        int day = 0;

        year = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[2], getPackets().get(0).getRawData()[3]});
        month = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[4]});
        day   = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[5]});

        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(new Date());
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);

        Date date = calBeginning.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            date = format.parse(String.format("%04d%02d%02d000000",year,month,day));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     return History Daily steps
     */
    public int getDailySteps()
    {
        int dailySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[4],
                getPackets().get(1).getRawData()[5],
                getPackets().get(1).getRawData()[6],
                getPackets().get(1).getRawData()[7]
        });

        return dailySteps;

    }
    /**
     return History Hourly steps
     */
    public ArrayList<Integer> getHourlySteps()
    {
        int hourlySteps  = 0;
        ArrayList<Integer> HourlySteps = new ArrayList<Integer>();

        //get every hour Steps:
        for (int i = 0; i<24; i++)
        {
            hourlySteps = 0;
            int offset = 14;
            if (getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset] != (byte)0xFF
                    && getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset+1] != (byte)0xFF
                    && getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset+2] != (byte)0xFF
                    && getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset+3] != (byte)0xFF)
            {
                //get hourly walk steps
                hourlySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset],
                        getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset+1]});
                //get hourly run steps
                hourlySteps += HexUtils.bytesToInt(new byte[]{getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset+2],
                        getPackets().get(HEADERLENGTH+i*HOURLYPACKETSNUMBER).getRawData()[offset+3]});


            }

            HourlySteps.add(i,hourlySteps);
        }
        return HourlySteps;

    }

    /**
     *
     * @return daily total distance, unit is in "meter"
     */

    public int getTotalDist() {

        int packetno = 1;
        int offset = 16;
        int dailyDist = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno).getRawData()[offset+2],
                getPackets().get(packetno).getRawData()[offset+3]
        });

        return dailyDist/100;

    }

    /**
     *
     * @return every hour distance, unit is in "meter"
     */
    public List<Integer> getHourlyDist() {

        int hourlyDisc  = 0;
        ArrayList<Integer> HourlyDist = new ArrayList<Integer>();

        //get every hour Disc:
        for (int i = 0; i<24; i++)
        {
            int packetno = HEADERLENGTH+i*HOURLYPACKETSNUMBER;
            int offset = 2;
            hourlyDisc = 0;
            if (getPackets().get(packetno).getRawData()[offset] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+1] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+2] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+3] != (byte)0xFF)
            {
                //walk
                hourlyDisc = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                        getPackets().get(packetno).getRawData()[offset+1],
                        getPackets().get(packetno).getRawData()[offset+2],
                        getPackets().get(packetno).getRawData()[offset+3]
                });
            }

            if (getPackets().get(packetno).getRawData()[offset+4] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+5] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+6] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+7] != (byte)0xFF)
            {
                //run
                hourlyDisc = hourlyDisc + HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset + 4],
                        getPackets().get(packetno).getRawData()[offset + 5],
                        getPackets().get(packetno).getRawData()[offset + 6],
                        getPackets().get(packetno).getRawData()[offset + 7]
                });
            }

            HourlyDist.add(i,hourlyDisc/100);
        }
        return HourlyDist;

    }

    /**
     *
     * @return daily calories consuming, unit is "kcal"
     */
    public int getTotalCalories() {
        int packetno = 2;
        int offset = 10;

        int dailyCalories = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno).getRawData()[offset+2],
                getPackets().get(packetno).getRawData()[offset+3]
        });

        return dailyCalories/1000;
    }

    public List<Integer> getHourlyCalories() {

        ArrayList<Integer> HourlyCalories = new ArrayList<Integer>();
        int hourlyCalories =0;

        //get every hour Calories:
        for (int i = 0; i<24; i++)
        {
            int packetno = HEADERLENGTH+i*HOURLYPACKETSNUMBER;
            int offset = 10;

            hourlyCalories = 0;

            if (getPackets().get(packetno).getRawData()[offset] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+1] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+2] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+3] != (byte)0xFF)
            {
                hourlyCalories = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                        getPackets().get(packetno).getRawData()[offset + 1],
                        getPackets().get(packetno).getRawData()[offset + 2],
                        getPackets().get(packetno).getRawData()[offset + 3]
                });
            }
            HourlyCalories.add(i, hourlyCalories / 1000);
        }
        return HourlyCalories;

    }

    /**
     *
     * @return total sleep time in a day, unit is "minute"
     */
    public int getTotalSleepTime() {
        int packetno = 3;
        int offset = 12;
        int value = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1]
        });
        return value;
    }



    public List<Integer> getHourlySleepTime() {
        ArrayList<Integer> HourlySleepTime = new ArrayList<Integer>();
        int hourlySleepTime =0;
        //get every hour SleepTime:
        for (int i = 0; i<24; i++)
        {
            int packetno = HEADERLENGTH+i*HOURLYPACKETSNUMBER+1;
            int offset = 6;
            hourlySleepTime = 0;
            if (getPackets().get(packetno).getRawData()[offset] != (byte)0xFF)
            {
                hourlySleepTime = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset]});
            }
            HourlySleepTime.add(i,hourlySleepTime);
        }
        return HourlySleepTime;
    }

    public int getTotalWakeTime() {
        int packetno = 3;
        int offset = 14;
        int value = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1]
        });
        return value;
    }


    public List<Integer> getHourlyWakeTime() {

        ArrayList<Integer> HourlyWakeTime = new ArrayList<Integer>();
        int hourlyWakeTime =0;
        //get every hour wake Time:
        for (int i = 0; i<24; i++)
        {
            int packetno = HEADERLENGTH+i*HOURLYPACKETSNUMBER+1;
            int offset = 7;
            hourlyWakeTime = 0;
            if (getPackets().get(packetno).getRawData()[offset] != (byte)0xFF)
            {
                hourlyWakeTime = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset]});
            }
            HourlyWakeTime.add(i,hourlyWakeTime);
        }
        return HourlyWakeTime;
    }


    public int getTotalLightTime() {
        int packetno = 3;
        int offset = 16;
        int value = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1]
        });
        return value;
    }


    public List<Integer> getHourlyLightTime() {

        ArrayList<Integer> HourlyLightTime = new ArrayList<Integer>();
        int hourlyLightTime =0;
        //get every hour wake Time:
        for (int i = 0; i<24; i++)
        {
            int packetno = HEADERLENGTH+i*HOURLYPACKETSNUMBER+1;
            int offset = 8;
            hourlyLightTime = 0;
            if (getPackets().get(packetno).getRawData()[offset] != (byte)0xFF)
            {
                hourlyLightTime = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset]});
            }
            HourlyLightTime.add(i,hourlyLightTime);
        }
        return HourlyLightTime;
    }


    public int getTotalDeepTime() {
        int packetno = 3;
        int offset = 18;
        int value = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1]
        });
        return value;
    }


    public List<Integer> getHourlDeepTime() {
        ArrayList<Integer> HourlyDeepTime = new ArrayList<Integer>();
        int hourlyDeepTime =0;
        //get every hour deep Time:
        for (int i = 0; i<24; i++)
        {
            int packetno = HEADERLENGTH+i*HOURLYPACKETSNUMBER+1;
            int offset = 9;
            hourlyDeepTime = 0;
            if (getPackets().get(packetno).getRawData()[offset] != (byte)0xFF)
            {
                hourlyDeepTime = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset]});
            }
            HourlyDeepTime.add(i,hourlyDeepTime);
        }
        return HourlyDeepTime;
    }

    /**
     *
     * @return some day 's step goal setting
     */
    public int getStepsGoal()
    {
        int stepGoal = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[6],
                getPackets().get(0).getRawData()[7],
                getPackets().get(0).getRawData()[8],
                getPackets().get(0).getRawData()[9]
        });

        return stepGoal;
    }
    /**
     return History Daily walk steps
     */
    public int getDailyWalkSteps()
    {
        int dailySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[8],
                getPackets().get(1).getRawData()[9],
                getPackets().get(1).getRawData()[10],
                getPackets().get(1).getRawData()[11]
        });

        return dailySteps;

    }

    /**
     return History Daily run steps
     */
    public int getDailyRunSteps()
    {
        int dailySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[12],
                getPackets().get(1).getRawData()[13],
                getPackets().get(1).getRawData()[14],
                getPackets().get(1).getRawData()[15]
        });

        return dailySteps;

    }

    /**
     * unit is "meter"
     * @return
     */
    public int getDailyWalkDistance()
    {
        int packetno = 2;
        int offset = 2;
        int dailyDist = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno).getRawData()[offset+2],
                getPackets().get(packetno).getRawData()[offset+3]
        });

        return dailyDist/100;
    }
    public int getDailyRunDistance()
    {
        int packetno = 2;
        int offset = 6;
        int dailyDist = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno).getRawData()[offset+2],
                getPackets().get(packetno).getRawData()[offset+3]
        });

        return dailyDist/100;
    }

    /**
     * unit is "minute"
     * @return
     */
    public int getDailyWalkDuration()
    {
        int packetno = 2;
        int offset = 18;
        int dailyDuration = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno+1).getRawData()[2],
                getPackets().get(packetno+1).getRawData()[3]
        });

        return dailyDuration/60;
    }

    public int getDailyRunDuration()
    {
        int packetno = 2;
        int offset = 14;
        int dailyDuration = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno).getRawData()[offset+2],
                getPackets().get(packetno).getRawData()[offset+3]
        });

        return dailyDuration/60;
    }

    /**
     * total inactivity time per day(in minutes)
     * @return
     */
    public int getInactivityTime() {
        int packetno = 3;
        int offset = 4;
        int value = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno).getRawData()[offset+2],
                getPackets().get(packetno).getRawData()[offset+3]
        });
        return value/60;

    }

    /**
     *
     * @return the harvesting of solar per day, unit is in minutes
     */
    public int getSolarHarvestingTime() {
        int packetno = 3;
        int offset = 8;
        int value = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],
                getPackets().get(packetno).getRawData()[offset+1],
                getPackets().get(packetno).getRawData()[offset+2],
                getPackets().get(packetno).getRawData()[offset+3]
        });
        return value/60;

    }

    /**
     *
     * @return  the harvesting of solar per hour, unit is in minutes
     */
    public List<Integer> getHourlyHarvestTime() {

        ArrayList<Integer> HourlyHarvestTime = new ArrayList<Integer>();
        int hourlyHarvestTime =0;
        //get every hour swim time:
        for (int i = 0; i<24; i++)
        {
            int packetno = HEADERLENGTH+i*HOURLYPACKETSNUMBER+1;
            int offset = 4;
            hourlyHarvestTime = 0;
            if (getPackets().get(packetno).getRawData()[offset] != (byte)0xFF
                    && getPackets().get(packetno).getRawData()[offset+1] != (byte)0xFF)
            {
                hourlyHarvestTime = HexUtils.bytesToInt(new byte[]{getPackets().get(packetno).getRawData()[offset],getPackets().get(packetno).getRawData()[offset+1]});
            }
            HourlyHarvestTime.add(i,hourlyHarvestTime/60);
        }
        return HourlyHarvestTime;
    }

    //end added
}
