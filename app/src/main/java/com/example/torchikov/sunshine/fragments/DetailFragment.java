package com.example.torchikov.sunshine.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
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
import com.example.torchikov.sunshine.activities.MainActivity;
import com.example.torchikov.sunshine.dataSet.WeatherDataSet;
import com.example.torchikov.sunshine.listeners.DataLoadSuccessfullyListener;
import com.example.torchikov.sunshine.utils.Utils;
import com.example.torchikov.sunshine.utils.WeatherLab;

import java.util.Date;


public class DetailFragment extends Fragment implements DataLoadSuccessfullyListener {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;
    private Callback mCallback;
    private TextView mDayTextView;
    private TextView mDateTextView;
    private TextView mHighTextView;
    private TextView mLowTextView;
    private ImageView mIconImageView;
    private TextView mForecastTextView;
    private TextView mHumidityTextView;
    private TextView mWindTextView;
    private TextView mPressureTextView;
    private WeatherDataSet mWeather;

    @Override
    public void updateUI() {
        if (mWeather == null) {
            mWeather = WeatherLab.getInstance(getActivity()).getForecastByDay(0);
        }
        mDayTextView.setText(Utils.getDayName(getActivity(), mWeather.getDate()));
        mDateTextView.setText(Utils.getFormattedMonthDay(getActivity(), mWeather.getDate()));
        mHighTextView.setText(mWeather.getHighTemperature());
        mLowTextView.setText(mWeather.getLowTemperature());
        Drawable drawable = getResources().getDrawable(Utils.getArtResourceForWeatherCondition(mWeather.getWeatherId()));
        mIconImageView.setImageDrawable(drawable);
        mForecastTextView.setText(mWeather.getForecast());
        String humidity = String.format(getString(R.string.format_humidity), mWeather.getHumidity(), "%");
        mHumidityTextView.setText(humidity);
        String windSpeed = String.format(getString(R.string.format_wind_speed_and_direction), mWeather.getWindSpeed(), mWeather.getWindDirection());
        mWindTextView.setText(windSpeed);
        String pressure = String.format(getString(R.string.format_pressure), mWeather.getPressure());
        mPressureTextView.setText(pressure);
        getActivity().invalidateOptionsMenu();

    }

    public interface Callback {
        void openSettings();
    }

    public static DetailFragment newInstance(int dayNum) {
        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_FORECAST, dayNum);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        WeatherLab.getInstance(getActivity()).subscribeToDataChanges(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (getArguments() != null) {
            int dayNum = getArguments().getInt(MainActivity.EXTRA_FORECAST);
            mWeather = WeatherLab.getInstance(getActivity()).getForecastByDay(dayNum);
        }

        mDayTextView = (TextView) rootView.findViewById(R.id.fragment_detail_day_textview);


        mDateTextView = (TextView) rootView.findViewById(R.id.fragment_detail_date_textview);


        mHighTextView = (TextView) rootView.findViewById(R.id.fragment_detail_high_textview);


        mLowTextView = (TextView) rootView.findViewById(R.id.fragment_detail_low_textview);


        mIconImageView = (ImageView) rootView.findViewById(R.id.fragment_detail_icon_imageview);


        mForecastTextView = (TextView) rootView.findViewById(R.id.fragment_detail_forecast_textview);


        mHumidityTextView = (TextView) rootView.findViewById(R.id.fragment_detail_humidity_textview);


        mWindTextView = (TextView) rootView.findViewById(R.id.fragment_detail_wind_textview);


        mPressureTextView = (TextView) rootView.findViewById(R.id.fragment_detail_pressure_textview);

        if (mWeather != null) {
            updateUI();
        }


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);

        if (mWeather != null) {
            MenuItem item = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }
    }
    //TODO: Когда меняю настройки измерения, они не меняются в forecastFragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                mCallback.openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mCallback = (Callback) getActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCallback = null;
    }

    private Intent createShareForecastIntent() {
        StringBuilder builder = new StringBuilder();
        builder.append(Utils.getDayName(getActivity(), mWeather.getDate()))
                .append(" ")
                .append(Utils.getFormattedMonthDay(getActivity(), mWeather.getDate()))
                .append(" ")
                .append(mWeather.getHighTemperature())
                .append("/")
                .append(mWeather.getLowTemperature())
                .append(" ")
                .append(mWeather.getForecast());
        String forecastString = builder.toString();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }
}
