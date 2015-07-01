package com.appology.mannercash.mannercash;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Jeong on 2015-07-01.
 */
public class GpsManager implements LocationListener {

    int mySpeed;

    public GpsManager(int mySpeed) {
        this.mySpeed = mySpeed;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mySpeed = (int) location.getSpeed();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
