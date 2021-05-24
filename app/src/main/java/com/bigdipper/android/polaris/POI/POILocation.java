package com.bigdipper.android.polaris.POI;

public class POILocation {

    private String name; // 이름
    private double radius; // 거리
    private String bizName; // 종류
    private String address; // 지번 주소
    private String roadAddress; // 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도

    public POILocation(String name, double radius, String bizName, String address, String roadAddress, double latitude, double longitude) {
        this.name = name;
        this.radius = radius;
        this.bizName = bizName;
        this.address = address;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getRadius() {
        return radius;
    }

    public String getBizName() {
        return bizName;
    }

    public String getAddress() {
        return address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
