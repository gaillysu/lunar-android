package com.medcorp.lunar.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;


public class DoublePressLeftkeyPacket extends Packet {
    public  final static  byte HEADER = 0x45;

    public DoublePressLeftkeyPacket(List<MEDRawData> packets) {
        super(packets);
    }
    /**
     return hotkey value
     Key Func
     0 - find my phone
     1 - music control(play/pause)
     2 - camera control
     */
    public byte getKeyFunction()
    {
        return getPackets().get(0).getRawData()[2];
    }
}
