package com.example.torchikov.sunshine.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.torchikov.sunshine.fragments.DetailFragment;
import com.example.torchikov.sunshine.R;
import com.example.torchikov.sunshine.dataSet.WeatherDataSet;

public class DetailActivity extends AppCompatActivity implements DetailFragment.Callback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState == null) {
            WeatherDataSet weather = (WeatherDataSet) getIntent().getSerializableExtra(MainActivity.EXTRA_FORECAST);
            getSupportFragmentManager().beginTransaction().add(R.id.weather_detail_container, DetailFragment.newInstance(weather)).commit();
        }


    }


    public static Intent newIntent(Context context, WeatherDataSet weather){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(MainActivity.EXTRA_FORECAST, weather);
        return intent;
    }

    @Override
    public void openSettings() {
        Intent intent = SettingsActivity.newIntent(this);
        startActivity(intent);
    }
}
