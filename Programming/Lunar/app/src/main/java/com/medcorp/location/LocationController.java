package com.medcorp.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.medcorp.event.LocationChangedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by med on 16/12/19.
 *
 */

public class LocationController implements LocationListener {
    private Context context;

    public LocationController(Context context) {
        this.context = context;
    }

    public void startUpdateLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LocationController", "no granted location permission@startUpdateLocation()");
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location == null) {
            Log.w("LocationController", "can't get location");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10 * 60, 1000 * 1000,
                    (LocationListener) this);
            return;
        }
        EventBus.getDefault().post(new LocationChangedEvent(location));
    }

    public void stopLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LocationController", "no granted location permission@stopLocation()");
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        EventBus.getDefault().post(new LocationChangedEvent(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.w("LocationController",provider + " location status is " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.w("LocationController",provider + " location is opened.");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LocationController", "no granted location permission@startUpdateLocation()");
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if(provider.equals(LocationManager.GPS_PROVIDER)){
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        else {
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location == null) {
            Log.w("LocationController", "not located when gps/network is opened");
            return;
        }
        EventBus.getDefault().post(new LocationChangedEvent(location));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.w("LocationController",provider + " location is closed.");
    }
}
