package com.example.torchikov.sunshine.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.torchikov.sunshine.listeners.DataLoadSuccessfullyListener;
import com.example.torchikov.sunshine.utils.Utils;
import com.example.torchikov.sunshine.utils.WeatherLab;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;


public class ForecastFragment extends Fragment implements DataLoadSuccessfullyListener {
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private static final String IS_TWO_PANE_ARG = "two_pane";


    private Callback mCallback;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean mTwoPane;

    public static ForecastFragment newInstance(boolean isTwoPane) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_TWO_PANE_ARG, isTwoPane);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void updateUI() {
        mAdapter.setWeathers(WeatherLab.getInstance(getActivity()).getWeathers());
        mAdapter.notifyDataSetChanged();
    }


    public interface Callback {
        void openWeatherDetail(int dayNum);

        void openSettings();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_forecast_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mAdapter = new ForecastAdapter();
        mAdapter.setWeathers(WeatherLab.getInstance(getActivity()).getWeathers());
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        WeatherLab.getInstance(getActivity()).subscribeToDataChanges(this);

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
                                WeatherLab.getInstance(getActivity()).setLatitude(String.valueOf(mLastLocation.getLatitude()));
                                WeatherLab.getInstance(getActivity()).setLongitude(String.valueOf(mLastLocation.getLongitude()));
                                WeatherLab.getInstance(getActivity()).loadForecastData();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    }).addApi(LocationServices.API)
                    .build();
        }

        mTwoPane = getArguments().getBoolean(IS_TWO_PANE_ARG);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!mTwoPane) {
            inflater.inflate(R.menu.forecast_fragment, menu);
        }
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

            if (mTwoPane) {
                drawable = getResources().getDrawable(Utils.getIconResourceForWeatherCondition(mWeather.getWeatherId()));
            } else {
                if (position == 0) {
                    drawable = getResources().getDrawable(Utils.getArtResourceForWeatherCondition(mWeather.getWeatherId()));
                } else {
                    drawable = getResources().getDrawable(Utils.getIconResourceForWeatherCondition(mWeather.getWeatherId()));
                }
            }

            mIconImageView.setImageDrawable(drawable);
            mDateTextView.setText(Utils.getDayName(getActivity(), mWeather.getDate()));
            mForecastTextView.setText(mWeather.getForecast());
            mHighTextView.setText(mWeather.getHighTemperature());
            mLowTextView.setText(mWeather.getLowTemperature());
        }

        @Override
        public void onClick(View v) {
            mCallback.openWeatherDetail(WeatherLab.getInstance(getActivity()).getWeathers().indexOf(mWeather));
        }
    }

    private class ForecastAdapter extends RecyclerView.Adapter<ForecastHolder> {
        private final int VIEW_TYPE_TODAY = 0;
        private final int VIEW_TYPE_FUTURE_DAY = 1;
        private List<WeatherDataSet> mWeathers;

        public void setWeathers(List<WeatherDataSet> weathers) {
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
