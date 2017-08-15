package com.unlp.tesis.steer.entities;

/**
 * Created by pedro on 18/10/16. Modificado 30/05
 */

public class Alert {

    private AlertType alertType;
    private float latitude;
    private float longitude;

    public Alert() {
    }

    public Alert(String description, float latitude, float longitude, AlertType alertType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.alertType = alertType;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
