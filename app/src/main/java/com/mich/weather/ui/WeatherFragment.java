package com.mich.weather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mich.weather.R;
import com.mich.weather.data.WeatherLocation;

public class WeatherFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView mLocationNameTextView;
    private TextView mCurrentTemperatureTextView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mLocationNameTextView = (TextView) rootView.findViewById(R.id.location_name);
        mCurrentTemperatureTextView = (TextView) rootView.findViewById(R.id.current_temperature);
        WeatherLocation data = getMainActivity().getCurrentWeatherData();
        if (data != null) {
            mLocationNameTextView.setText(data.getName());
            mCurrentTemperatureTextView.setText(data.getMain().getTemp());
        }
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    private MainActivity getMainActivity() {
        return (MainActivity)getActivity();
    }
}
