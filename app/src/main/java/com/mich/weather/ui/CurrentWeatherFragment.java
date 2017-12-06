package com.mich.weather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mich.weather.R;
import com.mich.weather.data.WeatherForecast;
import com.mich.weather.data.WeatherResponse;
import com.mich.weather.utils.DayFormatter;
import com.mich.weather.utils.Utils;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CurrentWeatherFragment extends Fragment {
    @SuppressWarnings("unused")
    @BindView(R.id.location_name)
    TextView mLocationNameTextView;
    @BindView(R.id.current_temperature)
    TextView mCurrentTemperatureTextView;
    @BindView(R.id.forecast_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.unknown_location_text)
    View mUnknownLocationText;
    @BindView(R.id.weather_header)
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
        boolean isValidData = data != null;
        if (isValidData) {
            mLocationNameTextView.setText(String.format("%s, %s", data.getCity().getName(), data.getCity().getCountry()));
            WeatherForecast now = DayFormatter.getCurrentForecast(data.getForecast());
            mCurrentTemperatureTextView.setText(Utils.formatTemperature(now.getMain().getTemperature()));

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            EasyRecyclerAdapter<WeatherForecast> adapter = new EasyRecyclerAdapter<>(this.getContext(), HourForecastHolder.class);
            mRecyclerView.setAdapter(adapter);
            adapter.addItems(data.getForecast());
        }

        int visible = isValidData ? View.VISIBLE : View.GONE;
        mWeatherContainerView.setVisibility(visible);
        mRecyclerView.setVisibility(visible);
        mUnknownLocationText.setVisibility(isValidData ? View.GONE : View.VISIBLE);
        return rootView;
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
