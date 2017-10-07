package com.unlp.tesis.steer.entities;

import java.util.List;

/**
 * Created by pedro on 18/10/16.
 */

public class PaidParkingArea {


    private String description;
    private float price;
    private int startTime;
    private int endTime;
    private List<GeofencePoint> geofencePoints;

    public PaidParkingArea() {
    }

    public PaidParkingArea(String description, float price, int startTime, int endTime, List<GeofencePoint> geofencePoints){
        this.description = description;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.geofencePoints = geofencePoints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public List<GeofencePoint> getGeofencePoints() {
        return geofencePoints;
    }

    public void setGeofencePoints(List<GeofencePoint> geofencePoints) {
        this.geofencePoints = geofencePoints;
    }

    public String toString()
    {
        return "Zona:" + this.description
                + " Horario de :" + this.startTime+ " a " + this.endTime
                + " Precio: $" +this.price+" por hora";
    }
}
