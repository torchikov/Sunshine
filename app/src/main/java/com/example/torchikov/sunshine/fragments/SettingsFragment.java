package com.example.torchikov.sunshine.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.torchikov.sunshine.R;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String KEY_PREF_LOCATION = "pref_location";
    private static final String KEY_PREF_UNITS = "pref_units";
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        Preference unitsPreference = getPreferenceScreen().findPreference(KEY_PREF_UNITS);

        bindPreferenceSummaryToValue(unitsPreference);




    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        bindPreferenceSummaryToValue(preference);

    }




    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void bindPreferenceSummaryToValue(Preference preference){
        String summary = getPreferenceScreen().getSharedPreferences().getString(preference.getKey(), "");

        preference.setSummary(summary);


    }
}
