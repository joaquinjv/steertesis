package com.unlp.tesis.steer.entities;

/**
 * Created by pedro on 18/10/16.
 */

public class PaidParkingArea {

    private float radius;
    private float latitude;
    private float longitude;
    private PaidParkingAreasType type;

    public PaidParkingArea() {
    }

    public PaidParkingArea(float radius, float latitude, float longitude, PaidParkingAreasType type) {
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type =type;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public PaidParkingAreasType getType() {
        return type;
    }

    public void setType(PaidParkingAreasType type) {
        this.type = type;
    }
}
