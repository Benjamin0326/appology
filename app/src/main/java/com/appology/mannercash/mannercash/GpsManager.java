package com.appology.mannercash.mannercash;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * Created by Jeong on 2015-07-01.
 */
public class GpsManager implements LocationListener {

    int mSpeed;
    double latitude;
    double longitude;
    String gpsStatus = "";
    boolean isProviderEnabled = false;

    public GpsManager() {
        mSpeed = 0;
        latitude = 0;
        longitude = 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mSpeed = (int) (location.getSpeed() * 3.6);
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

    public String getGpsStatus() {
        return gpsStatus;
    }

    public boolean isProviderEnabled() {
        return isProviderEnabled;
    }

    public void setIsProviderEnabled(boolean isProviderEnabled) {
        this.isProviderEnabled = isProviderEnabled;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch(status) {
            case LocationProvider.OUT_OF_SERVICE:
                gpsStatus = "서비스 지역이 아닙니다.";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                gpsStatus = "일시적으로 위치 정보를 사용할 수 없습니다.";
                break;
            case LocationProvider.AVAILABLE:
                gpsStatus = "서비스 가능 지역입니다.";
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        isProviderEnabled = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        isProviderEnabled = false;
    }
}
