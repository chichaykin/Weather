package com.mich.weather.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mich.weather.repositories.WeatherLocationPojo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


class SpinnerAdapter extends ArrayAdapter<WeatherLocationPojo> implements ThemedSpinnerAdapter {
    private static final int LAYOUT = android.R.layout.simple_list_item_1;
    private final ThemedSpinnerAdapter.Helper mDropDownHelper;
    private final LayoutInflater mInflater;

    public SpinnerAdapter(Context context) {
        super(context, LAYOUT, new ArrayList<WeatherLocationPojo>());
        mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            // Inflate the drop down using the helper's LayoutInflater
            LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
            view = inflater.inflate(LAYOUT, parent, false);
        } else {
            view = convertView;
        }

        WeatherLocationPojo location = getItem(position);
        StringBuilder builder = new StringBuilder(location.city);
        if (!StringUtils.isBlank(location.country)) {
            builder.append(",\u0009").append(location.country);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(builder.toString());

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = mInflater.inflate(LAYOUT, parent, false);
        } else{
            view = convertView;
        }

        WeatherLocationPojo location = getItem(position);
        StringBuilder builder = new StringBuilder(location.city);
        if (!StringUtils.isBlank(location.country)) {
            builder.append(",\u0009").append(location.country);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(builder.toString());

        return view;
    }

    @Override
    public Resources.Theme getDropDownViewTheme() {
        return mDropDownHelper.getDropDownViewTheme();
    }

    @Override
    public void setDropDownViewTheme(Resources.Theme theme) {
        mDropDownHelper.setDropDownViewTheme(theme);
    }
}
