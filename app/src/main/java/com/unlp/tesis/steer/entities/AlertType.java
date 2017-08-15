package com.unlp.tesis.steer.entities;

/**
 * author jvillalba
 */

public class AlertType {

    private String name;
    private String description;

    public AlertType() {
    }

    public AlertType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
