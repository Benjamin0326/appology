package com.appology.mannercash.mannercash;

/**
 * Created by Jeong on 2015-08-13.
 */
public class StaticVariable {
    static final int limitSpeedCount = 71;
    static final int gpsUpdateDistance = 50;
    static final int directionDataCount = 10000 / gpsUpdateDistance;
    static final int changeRoadStandByCount = 500 / gpsUpdateDistance;
    static final int pointSaveStandardDistance = 10;
    static final int pointSaveUnit = 6;
}
