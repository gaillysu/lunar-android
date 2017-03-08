package com.medcorp.lunar.model;

/**
 * Created by med on 16/8/5.
 */
public class ApplicationInfomation {
    /**
     * listNumber: 0~31 when updated
     *           :0x80  when created
     */
    private byte listNumber;
    /**
     * 5 bytes,LED1 - 8 on/off,	LED9-12,Vib on/off,	Colour_LED_Red,	Colour_LED_Green,	Colour_LED_Blue
     */
    private byte[] ledPattern;

    /**
     * application ID, use the package name
     */
    private String data;

    public ApplicationInfomation(byte listNumber,byte[] ledPattern,String data) {
        this.listNumber = listNumber;
        this.ledPattern = ledPattern;
        this.data = data;
    }

    public byte getListNumber() {
        return listNumber;
    }

    public byte[] getLedPattern() {
        return ledPattern;
    }

    public String getData() {
        return data;
    }
}
