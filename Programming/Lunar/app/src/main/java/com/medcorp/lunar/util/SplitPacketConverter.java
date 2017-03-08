package com.medcorp.lunar.util;

/**
 * Created by med on 16/5/20.
 * build packets
 */
public class SplitPacketConverter {
   static final int MTU = 20;

    /**
     *
     * @param rawData: all data exclude "command ID"
     * @param header: command ID
     * @return: [0...]
     *          [0xFF...]
     *
     *          OR
     *
     *          [0...]
     *          [1...]
     *          [2...]
     *          ...
     *          ...
     *          [0xFF...]
     */
    public static byte[][] rawData2Packets(byte[] rawData,byte header)
    {
            int packetsNum = rawData.length/(MTU-2);
            if(rawData.length%(MTU-2)!=0)
            {
                packetsNum = packetsNum + 1;
            }
            if(packetsNum<2) packetsNum = 2;

            byte [][] packages = new byte[packetsNum][MTU];
            int totalRead = 0;

            for(int i=0;i<packetsNum;i++)
            {
                //set packet frame SEQ, 1 byte
                if(i==packetsNum-1) {
                    packages[i][0] = (byte) (0xFF);
                }
                else {
                    packages[i][0] = (byte) i;
                }

                packages[i][1] = header;

                //set packet content, MTU-2 bytes
                for(int j=2;j<MTU;j++)
                {
                    if(totalRead==rawData.length)
                    {
                        break;
                    }
                    packages[i][j] = rawData[totalRead];
                    totalRead = totalRead + 1;
                }
            }
            return packages;
    }
}
