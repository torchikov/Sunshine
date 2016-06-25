package com.example.torchikov.sunshine.dataSet;


import java.io.Serializable;
import java.util.Date;

public class WeatherDataSet implements Serializable {
    private Date mDate;
    private String mForecast;
    private String mHighTemperature;
    private String mLowTemperature;
    private String mCityName;
    private double mPressure;
    private int mHumidity;
    private double mWindSpeed;
    private String mWindDirection;
    private int mWeatherId;


    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getForecast() {
        return mForecast;
    }

    public void setForecast(String forecast) {
        this.mForecast = forecast;
    }

    public String getHighTemperature() {
        return mHighTemperature;
    }

    public void setHighTemperature(String highTemperature) {
        this.mHighTemperature = highTemperature;
    }

    public String getLowTemperature() {
        return mLowTemperature;
    }

    public void setLowTemperature(String lowTemperature) {
        this.mLowTemperature = lowTemperature;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        this.mCityName = cityName;
    }

    public double getPressure() {
        return mPressure;
    }

    public void setPressure(double pressure) {
        mPressure = pressure;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int humidity) {
        mHumidity = humidity;
    }

    public double getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public String getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(String windDirection) {
        mWindDirection = windDirection;
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(int weatherId) {
        mWeatherId = weatherId;
    }
}
