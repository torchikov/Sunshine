package com.example.torchikov.sunshine.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.torchikov.sunshine.R;
import com.example.torchikov.sunshine.dataSet.WeatherDataSet;
import com.example.torchikov.sunshine.listeners.DataLoadSuccessfullyListener;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WeatherLab {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String API_KEY = "4bf7a51e8e1994d49b303213d9700041"; //Your API key

    private List<DataLoadSuccessfullyListener> mListeners = new LinkedList<>();
    private static WeatherLab sInstance;
    private List<WeatherDataSet> mWeathers = new ArrayList<>();
    private String mLatitude;
    private String mLongitude;
    private Context mContext;

    private WeatherLab(Context context) {
        mContext = context.getApplicationContext();
    }

    public static WeatherLab getInstance(Context context) {
        if (sInstance == null) {
            synchronized (WeatherLab.class) {
                if (sInstance == null) {
                    sInstance = new WeatherLab(context);
                }
            }
        }
        return sInstance;
    }

    public List<WeatherDataSet> getWeathers() {
        return mWeathers;
    }

    public void setWeathers(List<WeatherDataSet> weathers) {
        mWeathers = weathers;
    }


    private class FetchWeatherTask extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String defaultUnits = mContext.getResources().getString(R.string.pref_units_default_value);
            String units = preferences.getString(mContext.getString(R.string.pref_units_key), defaultUnits);
            String unitsRes;
            if (units.equals("Цельсий")) {
                unitsRes = "metric";
            } else {
                unitsRes = "imperial";
            }

            String format = "json";
            int numDays = 7;
            String langRus = "ru";


            final String LOCATION_LATITUDE = "lat";
            final String LOCATION_LONGITUDE = "lon";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "APPID";
            final String LANGUAGE = "lang";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(LANGUAGE, langRus)
                    .appendQueryParameter(LOCATION_LATITUDE, mLatitude)
                    .appendQueryParameter(LOCATION_LONGITUDE, mLongitude)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, unitsRes)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM, API_KEY)
                    .build();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJSON = null;

            try {
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                forecastJSON = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                mWeathers = new WeatherDataParser().getWeatherDataFromJson(mContext, forecastJSON);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing JSON", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            notifySubscribersWhatDataWasChanged();
        }
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public void subscribeToDataChanges(DataLoadSuccessfullyListener listener) {
        mListeners.add(listener);
    }

    private void notifySubscribersWhatDataWasChanged() {
        for (DataLoadSuccessfullyListener listener : mListeners) {
            listener.updateUI();
        }
        mListeners.clear();
    }

    public WeatherDataSet getForecastByDay(int dayNum) {
        return mWeathers.get(dayNum);
    }

    public void loadForecastData(){
        new FetchWeatherTask().execute();
    }
}
