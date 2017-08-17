package com.unlp.tesis.steer.entities;

/**
 * Created by pedro on 18/10/16. Modificado 30/05
 */

public class Alert {

    private AlertType alertType;
    private Double latitude;
    private Double longitude;

    public Alert() {
    }

    public Alert(String description, Double latitude, Double longitude, AlertType alertType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.alertType = alertType;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
