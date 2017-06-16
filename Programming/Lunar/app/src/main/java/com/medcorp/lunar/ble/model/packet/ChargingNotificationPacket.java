package com.medcorp.lunar.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;

/**
 * Created by med on 17/6/8.
 */
public class ChargingNotificationPacket extends Packet {
    public  final static  byte HEADER = 0x48;
    public ChargingNotificationPacket(List<MEDRawData> packets) {
        super(packets);
    }
}
