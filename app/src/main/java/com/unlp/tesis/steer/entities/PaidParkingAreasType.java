package com.unlp.tesis.steer.entities;

/**
 * Created by pedro on 27/04/17.
 */

public class PaidParkingAreasType {

    private String description;
    private float price;
    private int startTime;
    private int endTime;


    public PaidParkingAreasType() {
    }

    public PaidParkingAreasType(String description, float price, int startTime, int endTime){
        this.description = description;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
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
}
