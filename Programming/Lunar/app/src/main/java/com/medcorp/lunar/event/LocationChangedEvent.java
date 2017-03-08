package com.medcorp.lunar.event;

import android.location.Location;

/**
 * Created by med on 16/12/19.
 */

public class LocationChangedEvent {
    private final Location location;
    public LocationChangedEvent(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
