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
import com.example.torchikov.sunshine.utils.Utils;


public class DetailFragment extends Fragment {
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

    public interface Callback {
        void openSettings();
    }

    public static DetailFragment newInstance(WeatherDataSet weather) {
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.EXTRA_FORECAST, weather);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (getArguments() != null && getArguments().getSerializable(MainActivity.EXTRA_FORECAST) != null) {
            mWeather = (WeatherDataSet) getArguments().getSerializable(MainActivity.EXTRA_FORECAST);
        }
        mDayTextView = (TextView) rootView.findViewById(R.id.fragment_detail_day_textview);
        mDayTextView.setText(Utils.getDayName(getActivity(), mWeather.getDate()));

        mDateTextView = (TextView) rootView.findViewById(R.id.fragment_detail_date_textview);
        mDateTextView.setText(Utils.getFormattedMonthDay(getActivity(), mWeather.getDate()));

        mHighTextView = (TextView) rootView.findViewById(R.id.fragment_detail_high_textview);
        mHighTextView.setText(mWeather.getHighTemperature());

        mLowTextView = (TextView) rootView.findViewById(R.id.fragment_detail_low_textview);
        mLowTextView.setText(mWeather.getLowTemperature());

        mIconImageView = (ImageView) rootView.findViewById(R.id.fragment_detail_icon_imageview);
        Drawable drawable = getResources().getDrawable(Utils.getArtResourceForWeatherCondition(mWeather.getWeatherId()));
        mIconImageView.setImageDrawable(drawable);

        mForecastTextView = (TextView) rootView.findViewById(R.id.fragment_detail_forecast_textview);
        mForecastTextView.setText(mWeather.getForecast());

        mHumidityTextView = (TextView) rootView.findViewById(R.id.fragment_detail_humidity_textview);
        String humidity = String.format(getString(R.string.format_humidity), mWeather.getHumidity(), "%");
        mHumidityTextView.setText(humidity);

        mWindTextView = (TextView) rootView.findViewById(R.id.fragment_detail_wind_textview);
        String windSpeed = String.format(getString(R.string.format_wind_speed_and_direction), mWeather.getWindSpeed(), mWeather.getWindDirection());
        mWindTextView.setText(windSpeed);

        mPressureTextView = (TextView) rootView.findViewById(R.id.fragment_detail_pressure_textview);
        String pressure = String.format(getString(R.string.format_pressure), mWeather.getPressure());
        mPressureTextView.setText(pressure);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

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
