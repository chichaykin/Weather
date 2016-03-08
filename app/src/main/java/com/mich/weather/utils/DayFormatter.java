package com.mich.weather.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mich.weather.R;
import com.mich.weather.data.WeatherForecast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Formatter helper class.
 */
public class DayFormatter {
    public final static long MILLISECONDS_IN_SECONDS = 1000;
    private final Context mContext;
    private final DateFormat mFormat = DateFormat.getDateTimeInstance();
    private final DateFormat mTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    public DayFormatter(Context context) {
        mContext = context;
    }

    /***
     * Find the closest {@code WeatherForecast}  to now.
     * @param list is not empty list of forecasts for several days.
     * @return {@code WeatherForecast}
     */
    public static WeatherForecast getCurrentForecast(@NonNull List<WeatherForecast> list) {
        final long nowSec = new Date().getTime() / MILLISECONDS_IN_SECONDS;
        long min = Integer.MAX_VALUE;
        WeatherForecast ret = null;
        for (WeatherForecast forecast : list) {

            long test = Math.abs(nowSec - forecast.getTimestamp());
            if (min > test) {
                ret = forecast;
                min = test;
            }
        }
        return ret;
    }

    /**
     * Format a Unix timestamp into a human readable week day String such as "Today", "Tomorrow"
     * and "Wednesday"
     */
    public String format(final long unixTimestamp) {
        final long milliseconds = unixTimestamp * MILLISECONDS_IN_SECONDS;
        String day;

        if (isToday(milliseconds)) {
            day = mContext.getResources().getString(R.string.today);
        } else if (isTomorrow(milliseconds)) {
            day = mContext.getResources().getString(R.string.tomorrow);
        } else {
            day = getDayOfWeek(milliseconds);
        }

        return day;
    }

    private String getDayOfWeek(final long milliseconds) {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(milliseconds));
    }

    private boolean isToday(final long milliseconds) {
        final SimpleDateFormat dayInYearFormat = new SimpleDateFormat("yyyyD", Locale.getDefault());
        final String nowHash = dayInYearFormat.format(new Date());
        final String comparisonHash = dayInYearFormat.format(new Date(milliseconds));
        return nowHash.equals(comparisonHash);
    }

    private boolean isTomorrow(final long milliseconds) {
        final SimpleDateFormat dayInYearFormat = new SimpleDateFormat("yyyyD", Locale.getDefault());
        final int tomorrowHash = Integer.parseInt(dayInYearFormat.format(new Date())) + 1;
        final int comparisonHash = Integer.parseInt(dayInYearFormat.format(new Date(milliseconds)));
        return comparisonHash == tomorrowHash;
    }

    @SuppressWarnings("unused")
    public String formatDate(final long unixTimestamp) {
        final long milliseconds = unixTimestamp * MILLISECONDS_IN_SECONDS;
        return mFormat.format(new Date(milliseconds));
    }

    public String formatTime(final long unixTimestamp) {
        final long milliseconds = unixTimestamp * MILLISECONDS_IN_SECONDS;
        return mTimeFormat.format(new Date(milliseconds));
    }
}
