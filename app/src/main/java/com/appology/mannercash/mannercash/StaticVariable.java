package com.appology.mannercash.mannercash;

/**
 * Created by Jeong on 2015-08-13.
 */
public class StaticVariable {
    static final int limitSpeedCount = 71;
    static final int directionDataCount = 100;
    static final int gpsUpdateDistance = 50;
    static final int changeRoadStandByCount = 500 / gpsUpdateDistance;
    static final int pointSaveStandardDistance = (200 * gpsUpdateDistance) / 1000;
    static final int pointSaveUnit = 6;
}
