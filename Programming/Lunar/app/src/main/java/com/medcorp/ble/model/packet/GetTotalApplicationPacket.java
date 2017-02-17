package com.medcorp.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;

/**
 * Created by med on 16/8/5.
 */
public class GetTotalApplicationPacket extends Packet {
    public GetTotalApplicationPacket(List<MEDRawData> packets) {
        super(packets);
    }

    /**
     *
     * @return total stored apps:max 32 apps
     */
    public byte getTotalApplicationID()
    {
        return getPackets().get(0).getRawData()[2];
    }
}
