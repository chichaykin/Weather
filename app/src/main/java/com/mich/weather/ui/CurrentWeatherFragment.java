package com.mich.weather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mich.weather.R;
import com.mich.weather.data.WeatherForecast;
import com.mich.weather.data.WeatherResponse;
import com.mich.weather.utils.DayFormatter;
import com.mich.weather.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CurrentWeatherFragment extends Fragment {
    @SuppressWarnings("unused")
    @Bind(R.id.location_name)
    TextView mLocationNameTextView;
    @Bind(R.id.current_temperature)
    TextView mCurrentTemperatureTextView;
    @Bind(R.id.forecast_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.unknown_location_text)
    View mUnknownLocationText;
    @Bind(R.id.weather_container_view)
    View mWeatherContainerView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CurrentWeatherFragment newInstance() {
        return new CurrentWeatherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hourly_forecast_current_day, container, false);
        ButterKnife.bind(this, rootView);
        WeatherResponse data = getMainActivity().getCurrentWeatherData();
        if (data != null) {
            mWeatherContainerView.setVisibility(View.VISIBLE);
            mUnknownLocationText.setVisibility(View.GONE);
            mLocationNameTextView.setText(String.format("%s, %s", data.getCity().getName(), data.getCity().getCountry()));
            WeatherForecast now = DayFormatter.getCurrentForecast(data.getForecast());
            mCurrentTemperatureTextView.setText(Utils.formatTemperature(now.getMain().getTemperature()));

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            EasyRecyclerAdapter<WeatherForecast> adapter = new EasyRecyclerAdapter<>(this.getContext(), HourForecastHolder.class);
            mRecyclerView.setAdapter(adapter);
            adapter.addItems(data.getForecast());
        } else {
            mWeatherContainerView.setVisibility(View.GONE);
            mUnknownLocationText.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
