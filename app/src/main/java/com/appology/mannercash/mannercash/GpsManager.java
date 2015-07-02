package com.appology.mannercash.mannercash;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Jeong on 2015-07-01.
 */
public class GpsManager implements LocationListener {

    int mSpeed;
    double latitude;
    double longitude;

    public GpsManager() {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mSpeed = (int) location.getSpeed();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public int getMSpeed() {
        return mSpeed;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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
