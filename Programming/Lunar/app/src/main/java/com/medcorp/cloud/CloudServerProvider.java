package com.medcorp.cloud;

/**
 * Created by med on 16/8/25.
 * 
 */
public enum CloudServerProvider {
    Validic(1),Med(2),All(0xFF);
    private int rawValue;
    CloudServerProvider(int rawValue) {
        this.rawValue = rawValue;
    }
    public int getRawValue() {
        return rawValue;
    }
}
