package com.example.torchikov.sunshine.utils;


import android.content.Context;
import android.speech.RecognitionService;
import android.support.annotation.DrawableRes;

import com.example.torchikov.sunshine.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String DATE_FORMAT = "yyyyMMdd";

    public static String getFriendlyDayString(Context context, Date date) {
        Calendar calendar = Calendar.getInstance();

        int currentJulianDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date);
        int julianDay = calendar.get(Calendar.DAY_OF_YEAR);

        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, date)
            ));
        } else if (julianDay < currentJulianDay + 7) {
            return getDayName(context, date);
        } else {
            SimpleDateFormat shortedDateFormat = new SimpleDateFormat("EEE MMM dd", Locale.getDefault());
            return shortedDateFormat.format(date);
        }
    }

    public static String getFormattedMonthDay(Context context, Date date) {
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());


        return monthDayFormat.format(date);
    }

    public static String getDayName(Context context, Date date) {

        Calendar calendar = Calendar.getInstance();
        int currentJulianDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date);
        int julianDay = calendar.get(Calendar.DAY_OF_YEAR);

        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            String result = dayFormat.format(date);

            return result.substring(0, 1).toUpperCase() + result.substring(1);
        }
    }

    public static String formatTemperature(Context context, double temperature) {
        return context.getString(R.string.format_temperature, temperature);
    }

    @DrawableRes
    public static int getIconResourceForWeatherCondition(int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId < 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    @DrawableRes
    public static int getArtResourceForWeatherCondition(int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId < 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }
}
