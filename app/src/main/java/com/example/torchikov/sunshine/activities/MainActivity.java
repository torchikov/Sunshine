package com.example.torchikov.sunshine.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.torchikov.sunshine.fragments.ForecastFragment;
import com.example.torchikov.sunshine.R;
import com.example.torchikov.sunshine.dataSet.WeatherDataSet;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {
    public static final String EXTRA_FORECAST = "forecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_logo);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new ForecastFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

    }

    @Override
    public void openWeatherDetail(WeatherDataSet weather) {
        startActivity(DetailActivity.newIntent(this, weather));
    }

    @Override
    public void openSettings() {
        Intent intent = SettingsActivity.newIntent(this);
        startActivity(intent);
    }
}
