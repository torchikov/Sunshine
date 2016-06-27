package com.example.torchikov.sunshine.utils;


public class WeatherLab {
    private static WeatherLab instance;

    private WeatherLab(){

    }

    public static WeatherLab getInstance(){
        if (instance == null){
            synchronized (WeatherLab.class){
                if (instance == null){
                    instance = new WeatherLab();
                }
            }
        }
        return instance;
    }
}
