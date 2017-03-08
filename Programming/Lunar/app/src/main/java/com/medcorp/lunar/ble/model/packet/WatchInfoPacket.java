package com.medcorp.lunar.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;

/**
 * Created by med on 16/7/25.
 */
public class WatchInfoPacket extends Packet {
    public WatchInfoPacket(List<MEDRawData> packets) {
        super(packets);
    }

    /**
     * Watch ID
     Watch ID
     1 - Nevo
     2 - Nevo Solar
     3- Lunar
     * @return
     */
    public byte getWatchID()
    {
        return getPackets().get(0).getRawData()[2];
    }

    /**
     * Model Number
     1 - Paris
     2 -  New York
     3 -  ShangHai
     ……
     * @return
     */
    public byte getWatchModel()
    {
        return getPackets().get(0).getRawData()[5];
    }
}
