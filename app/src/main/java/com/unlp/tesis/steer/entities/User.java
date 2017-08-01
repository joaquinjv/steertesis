package com.unlp.tesis.steer.entities;

/**
 * Created by pedro on 18/10/16. Modificado 30/05
 */

public class User {

    private String Name;
    private String Email;
    private String Plate;

    public User() {
    }

    public User(String name, String email, String plate) {

        Name = name;
        Email = email;
        Plate = plate;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPlate() {
        return Plate;
    }

    public void setPlate(String plate) {
        Plate = plate;
    }
}
