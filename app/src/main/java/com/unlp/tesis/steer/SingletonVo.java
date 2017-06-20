package com.unlp.tesis.steer;

import android.content.Context;

/**
 * Created by mirrorlink on 6/18/17.
 */

public class SingletonVo {

    private static SingletonVo instance = null;
    private static String tokenAthentication = null;

    //a private constructor so no instances can be made outside this class
    private SingletonVo() {}

    //Everytime you need an instance, call this
    public static SingletonVo getInstance() {
        if(instance == null)
            instance = new SingletonVo();

        return instance;
    }

    public void setTokenAthentication(String token) {
        this.tokenAthentication = token;
    }
    public String getTokenAthentication() {
        return tokenAthentication;
    }
}