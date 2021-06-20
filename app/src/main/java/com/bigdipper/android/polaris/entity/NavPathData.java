package com.bigdipper.android.polaris.entity;

//use for navPath Data
public class NavPathData {
    private String index;
    private String latitude;
    private String longitude;
    private String turnType;

    public NavPathData(String index, String latitude, String longitude, String turnType){
        this.index = index;
        this.latitude = latitude;
        this.longitude = longitude;
        this.turnType = turnType;
    }

    public String getIndex() {
        return index;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTurnType() {
        return turnType;
    }

    public void setTurnType(String turnType) {
        this.turnType = turnType;
    }
}
