package com.unlp.tesis.steer.entities;

/**
 * Created by Pedro Rodriguez on 15/3/2018.
 */

public class Position {
    private Double Latitude;
    private Double Longitude;

    public Position() {
    }

    public Position(Double latitude, Double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }


    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        this.Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        this.Longitude = longitude;
    }
}
