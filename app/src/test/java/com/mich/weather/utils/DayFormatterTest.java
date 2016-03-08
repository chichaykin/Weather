package com.mich.weather.utils;


import com.mich.weather.data.WeatherForecast;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class DayFormatterTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void getCurrentForecastTestOnFail() {
        DayFormatter.getCurrentForecast(null);
    }

    @Test
    public void getCurrentForecastTestEmptyList() {
        List<WeatherForecast> list = new ArrayList<>();
        Assert.assertNull(DayFormatter.getCurrentForecast(list));
    }

    @Test
    public void getCurrentForecastTest() {
        List<WeatherForecast> list = new ArrayList<>();
        list.add(new WeatherForecast(10000));
        WeatherForecast expect = new WeatherForecast(20000);
        list.add(expect);
        Assert.assertSame(expect, DayFormatter.getCurrentForecast(list));
    }

    @Test
    public void getCurrentForecastTest2() {
        long nowTimeStamp = new Date().getTime() / DayFormatter.MILLISECONDS_IN_SECONDS;
        List<WeatherForecast> list = new ArrayList<>();
        WeatherForecast expect = new WeatherForecast(nowTimeStamp - 100);
        list.add(expect);
        list.add(new WeatherForecast(nowTimeStamp + 200));
        Assert.assertSame(expect, DayFormatter.getCurrentForecast(list));
    }
}
