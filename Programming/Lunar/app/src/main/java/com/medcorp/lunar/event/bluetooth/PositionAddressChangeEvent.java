package com.medcorp.lunar.event.bluetooth;

import android.location.Address;

/**
 * Created by Jason on 2017/1/12.
 */

public class PositionAddressChangeEvent {

    private Address mAddress;

    public PositionAddressChangeEvent(Address address){
        this.mAddress = address;
    }

    public Address getAddress(){
        return mAddress;
    }
}
