package com.unlp.tesis.steer.entities;

/**
 * Created by Pedro on 02/10/2017.
 */

public class GeofencePoint {
    private int radius;
    private double latitude;
    private double longitude;
    private String paidParkingAreaId;

    public GeofencePoint() {
    }

    public GeofencePoint(int radius, double latitude, double longitude, String paidParkingAreaId) {
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        this.paidParkingAreaId = paidParkingAreaId;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPaidParkingAreaId() {
        return paidParkingAreaId;
    }

    public void setPaidParkingAreaId(String paidParkingAreaId) {
        this.paidParkingAreaId = paidParkingAreaId;
    }
}
