package com.example.torchikov.sunshine.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.torchikov.sunshine.R;
import com.example.torchikov.sunshine.dataSet.WeatherDataSet;
import com.example.torchikov.sunshine.utils.Utils;
import com.example.torchikov.sunshine.utils.WeatherDataParser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ForecastFragment extends Fragment {
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private static final String API_KEY = ""; //Your API key
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";


    private Callback mCallback;
    private RecyclerView mRecyclerView;
    private List<WeatherDataSet> mWeathers = new ArrayList<>();
    private ForecastAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitude;
    private String mLongitude;

    public interface Callback {
        void openWeatherDetail(WeatherDataSet weather);

        void openSettings();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_forecast_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new ForecastAdapter(mWeathers);
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                                //TODO: Обработать, что нет разрешения
                            }
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                            if (mLastLocation != null) {
                                mLatitude = String.valueOf(mLastLocation.getLatitude());
                                mLongitude = String.valueOf(mLastLocation.getLongitude());
                            }
                            updateWeather();

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    }).addApi(LocationServices.API)
                    .build();
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                mCallback.openSettings();
                return true;
            default:
                return true;
        }
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, List<WeatherDataSet>> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected List<WeatherDataSet> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            List<WeatherDataSet> weathers = new ArrayList<>();

            String format = "json";
            String units = params[0];
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
                    .appendQueryParameter(UNITS_PARAM, units)
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
                weathers = new WeatherDataParser().getWeatherDataFromJson(getActivity(), forecastJSON, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing JSON", e);
            }
            return weathers;
        }

        @Override
        protected void onPostExecute(List<WeatherDataSet> weathers) {

            if (!weathers.isEmpty()) {
                mWeathers.addAll(weathers);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defaultUnits = getResources().getString(R.string.pref_units_default_value);
        String units = preferences.getString(getString(R.string.pref_units_key), defaultUnits);
        String unitsRes;
        if (units.equals("Цельсий")){
            unitsRes = "metric";
        } else {
            unitsRes = "imperial";
        }
        new FetchWeatherTask().execute(unitsRes);
    }


    private class ForecastHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView mDateTextView;
        private TextView mForecastTextView;
        private TextView mHighTextView;
        private TextView mLowTextView;
        private ImageView mIconImageView;

        private WeatherDataSet mWeather;

        public ForecastHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_date_textview);
            mForecastTextView = (TextView) itemView.findViewById(R.id.list_item_forecast_textview);
            mHighTextView = (TextView) itemView.findViewById(R.id.lit_item_high_textview);
            mLowTextView = (TextView) itemView.findViewById(R.id.list_item_low_textview);
            mIconImageView = (ImageView) itemView.findViewById(R.id.list_item_icon_imageview);
        }

        public void bindWeather(WeatherDataSet weather, int position) {
            mWeather = weather;
            Drawable drawable = null;

            if (position == 0) {
                drawable = getResources().getDrawable(Utils.getArtResourceForWeatherCondition(mWeather.getWeatherId()));
            } else {
                drawable = getResources().getDrawable(Utils.getIconResourceForWeatherCondition(mWeather.getWeatherId()));
            }

            mIconImageView.setImageDrawable(drawable);
            mDateTextView.setText(Utils.getDayName(getActivity(), mWeather.getDate()));
            mForecastTextView.setText(mWeather.getForecast());
            mHighTextView.setText(mWeather.getHighTemperature());
            mLowTextView.setText(mWeather.getLowTemperature());
        }

        @Override
        public void onClick(View v) {
            mCallback.openWeatherDetail(mWeather);
        }
    }

    private class ForecastAdapter extends RecyclerView.Adapter<ForecastHolder> {
        private final int VIEW_TYPE_TODAY = 0;
        private final int VIEW_TYPE_FUTURE_DAY = 1;
        private List<WeatherDataSet> mWeathers;

        public ForecastAdapter(List<WeatherDataSet> weathers) {
            mWeathers = weathers;
        }

        @Override
        public ForecastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = null;
            switch (viewType) {
                case VIEW_TYPE_TODAY:
                    view = inflater.inflate(R.layout.list_item_forecast_today, parent, false);
                    break;
                case VIEW_TYPE_FUTURE_DAY:
                    view = inflater.inflate(R.layout.list_item_forecast, parent, false);
                    break;
            }

            return new ForecastHolder(view);
        }

        @Override
        public void onBindViewHolder(ForecastHolder holder, int position) {
            WeatherDataSet weather = mWeathers.get(position);
            holder.bindWeather(weather, position);
        }

        @Override
        public int getItemCount() {
            return mWeathers.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
        }


    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


}
