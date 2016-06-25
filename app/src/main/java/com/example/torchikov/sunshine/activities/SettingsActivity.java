package com.example.torchikov.sunshine.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.torchikov.sunshine.R;
import com.example.torchikov.sunshine.fragments.SettingsFragment;


public class SettingsActivity extends AppCompatActivity {

    public static Intent newIntent(Context context){
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_settings_container);

        if (fragment == null){
            fragment = SettingsFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.fragment_settings_container, fragment).commit();
        }
    }
}
