package com.medcorp.lunar.ble.model.packet;


import com.medcorp.lunar.model.ApplicationInfomation;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;

/**
 * Created by med on 16/8/9.
 */
public class NewApplicationArrivedPacket extends Packet {
    public  final static  byte HEADER = 0x55;
    public NewApplicationArrivedPacket(List<MEDRawData> packets) {
        super(packets);
    }
    public byte getTotalApplications()
    {
        return getPackets().get(0).getRawData()[2];
    }
    public ApplicationInfomation getApplicationInfomation()
    {
        byte dataLength = getPackets().get(0).getRawData()[3];
        byte[] byteData = new byte[dataLength];
        int firstRawDataBytes = 16;//or 15???
        if(dataLength<=firstRawDataBytes)
        {
            System.arraycopy(getPackets().get(0).getRawData(),20-firstRawDataBytes,byteData,0,dataLength);
        }
        else{
            int totalDataRaws = 1 + ((dataLength -firstRawDataBytes)%18 == 0?(dataLength -firstRawDataBytes)/18 : (dataLength -firstRawDataBytes)/18+1);
            int copyLength = 0;
            for(int i =0;i<totalDataRaws;i++)
            {
                if(i==0)
                {
                    System.arraycopy(getPackets().get(i).getRawData(),20-firstRawDataBytes,byteData,0,firstRawDataBytes);
                    copyLength = firstRawDataBytes;
                }
                else if(i==totalDataRaws-1)
                {
                    System.arraycopy(getPackets().get(i).getRawData(),2,byteData,copyLength,dataLength-copyLength);
                }
                else {
                    System.arraycopy(getPackets().get(i).getRawData(),2,byteData,copyLength,18);
                    copyLength = copyLength + 18;
                }
            }
        }
        return new ApplicationInfomation((byte)0x80,new byte[]{0,0,0,0,0},new String(byteData));
    }
}