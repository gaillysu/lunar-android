package com.medcorp.lunar.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;

/**
 * Created by med on 16/8/5.
 */
public class EditApplicationPacket extends Packet {
    public EditApplicationPacket(List<MEDRawData> packets) {
        super(packets);
    }

    /**
     @return Status
     0 - OK
     1 - Flash Busy
     2 - Low Battery
     3 - List not Exist
     4:  List full
     */
    public byte getEditApplicationStatus()
    {
        return getPackets().get(0).getRawData()[2];
    }
}
