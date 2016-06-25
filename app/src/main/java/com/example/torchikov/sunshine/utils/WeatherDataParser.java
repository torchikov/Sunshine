package com.example.torchikov.sunshine.utils;


import android.content.Context;
import android.content.Intent;

import com.example.torchikov.sunshine.R;
import com.example.torchikov.sunshine.dataSet.WeatherDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WeatherDataParser {
    private static final String LOG_TAG = WeatherDataParser.class.getSimpleName();

    public List<WeatherDataSet> getWeatherDataFromJson(Context context, String jsonString, int numDays) throws JSONException {
        List<WeatherDataSet> result = new ArrayList<>();
        final String OWN_LIST = "list";
        final String OWN_WEATHER = "weather";
        final String OWN_TEMPERATURE = "temp";
        final String OWN_MAX = "max";
        final String OWN_MIN = "min";
        final String OWN_DESCRIPTION = "description";
        final String OWN_DATE = "dt";
        final String OWN_CITY = "city";
        final String OWN_CITY_NAME = "name";
        final String OWN_HUMIDITY = "humidity";
        final String OWN_PRESSURE = "pressure";
        final String OWN_WIND_SPEED = "speed";
        final String OWN_WIND_DIRECTION ="deg";
        final String OWN_WEATHER_ID = "id";


        JSONObject forecastJson = new JSONObject(jsonString);
        JSONArray weatherArray = forecastJson.getJSONArray(OWN_LIST);

        for (int i = 0; i < weatherArray.length(); i++) {
            Date date;
            String forecast;
            String highTemperature;
            String lowTemperature;
            String cityName;
            String windDirection;
            int weatherId;
            int humidity;
            double pressure;
            double windSpeed;
            double windDegrees;

            JSONObject dayForecast = weatherArray.getJSONObject(i);
            date = new Date(dayForecast.getLong(OWN_DATE) * 1000);


            humidity = dayForecast.getInt(OWN_HUMIDITY);
            pressure = dayForecast.getDouble(OWN_PRESSURE);
            pressure = pressure * 0.75;
            windSpeed = dayForecast.getDouble(OWN_WIND_SPEED);
            windDegrees = dayForecast.getDouble(OWN_WIND_DIRECTION);
            windDirection = getWindDirection(context, windDegrees);



            JSONObject weatherObject = dayForecast.getJSONArray(OWN_WEATHER).getJSONObject(0);
            forecast = weatherObject.getString(OWN_DESCRIPTION);
            forecast = forecast.substring(0, 1).toUpperCase() + forecast.substring(1);
            weatherId = weatherObject.getInt(OWN_WEATHER_ID);

            JSONObject cityObject = forecastJson.getJSONObject(OWN_CITY);


            cityName = cityObject.getString(OWN_CITY_NAME);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWN_TEMPERATURE);
            double high = temperatureObject.getDouble(OWN_MAX);
            double low = temperatureObject.getDouble(OWN_MIN);

            highTemperature = Utils.formatTemperature(context, high);
            lowTemperature = Utils.formatTemperature(context, low);

            WeatherDataSet weather = new WeatherDataSet();
            weather.setDate(date);

            weather.setForecast(forecast);
            weather.setHighTemperature(highTemperature);
            weather.setLowTemperature(lowTemperature);
            weather.setCityName(cityName);
            weather.setHumidity(humidity);
            weather.setPressure(pressure);
            weather.setWindSpeed(windSpeed);
            if (windDirection != null) {
                weather.setWindDirection(windDirection);
            } else {
                weather.setWindDirection("");
            }
            weather.setWeatherId(weatherId);
            result.add(weather);
        }

        return result;

    }

    private String getWindDirection(Context context, double windDirectionOnDegrees) {
        if (windDirectionOnDegrees >= 350 && windDirectionOnDegrees <= 10){
            return context.getString(R.string.north);
        }else if (windDirectionOnDegrees >= 170 && windDirectionOnDegrees <= 190){
            return context.getString(R.string.south);
        } else if (windDirectionOnDegrees >= 80 && windDirectionOnDegrees <= 100){
            return context.getString(R.string.east);
        } else if (windDirectionOnDegrees >= 260 && windDirectionOnDegrees <= 280){
            return context.getString(R.string.west);
        } else if (windDirectionOnDegrees >= 10 && windDirectionOnDegrees <= 80){
            return context.getString(R.string.north_east);
        }else if (windDirectionOnDegrees >= 100 && windDirectionOnDegrees <= 170){
            return context.getString(R.string.south_east);
        }else if (windDirectionOnDegrees >= 190 && windDirectionOnDegrees <= 260){
            return context.getString(R.string.south_west);
        } else if (windDirectionOnDegrees >= 280 && windDirectionOnDegrees <= 350){
            return context.getString(R.string.north_west);
        } else {
            return null;
        }
    }


}
