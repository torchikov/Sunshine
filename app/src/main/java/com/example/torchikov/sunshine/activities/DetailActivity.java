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
            int dayNum = getIntent().getIntExtra(MainActivity.EXTRA_FORECAST, -1);

            if (dayNum >= 0) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, DetailFragment.newInstance(dayNum)).commit();
            }else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, DetailFragment.newInstance()).commit();
            }
        }


    }


    public static Intent newIntent(Context context, int dayNum){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(MainActivity.EXTRA_FORECAST, dayNum);
        return intent;
    }

    @Override
    public void openSettings() {
        Intent intent = SettingsActivity.newIntent(this);
        startActivity(intent);
    }
}
