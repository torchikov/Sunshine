package com.example.torchikov.sunshine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.torchikov.sunshine.fragments.DetailFragment;
import com.example.torchikov.sunshine.fragments.ForecastFragment;
import com.example.torchikov.sunshine.R;
import com.example.torchikov.sunshine.dataSet.WeatherDataSet;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback, DetailFragment.Callback {
    public static final String EXTRA_FORECAST = "forecast";
    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_logo);




        if (findViewById(R.id.weather_detail_container) != null){
            mTwoPane = true;

            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, DetailFragment.newInstance(), DETAIL_FRAGMENT_TAG).commit();
            }
        }else {
            mTwoPane = false;
        }

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_forecast_container, ForecastFragment.newInstance(mTwoPane)).commit();
        }

    }

    @Override
    public void openWeatherDetail(int dayNum) {
        if (!mTwoPane) {
            startActivity(DetailActivity.newIntent(this, dayNum));
        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, DetailFragment.newInstance(dayNum), DETAIL_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public void openSettings() {
            Intent intent = SettingsActivity.newIntent(this);
            startActivity(intent);

    }
}
