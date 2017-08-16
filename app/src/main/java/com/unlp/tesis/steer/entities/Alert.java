package com.unlp.tesis.steer.entities;

/**
 * Created by pedro on 18/10/16. Modificado 30/05
 */

public class Alert {

    private AlertType alertType;
    private String latitude;
    private String longitude;

    public Alert() {
    }

    public Alert(String description, String latitude, String longitude, AlertType alertType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.alertType = alertType;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
