package com.mich.weather.ui;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mich.weather.R;
import com.mich.weather.data.Main;
import com.mich.weather.data.Weather;
import com.mich.weather.data.WeatherForecast;
import com.mich.weather.utils.DayFormatter;
import com.mich.weather.utils.L;
import com.mich.weather.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@SuppressWarnings({"WeakerAccess", "unused"})
@LayoutId(R.layout.hour_forecast)
public class HourForecastHolder extends ItemViewHolder<WeatherForecast> {

    @ViewId(R.id.week_text)
    TextView mDayOfWeek;

    @ViewId(R.id.time)
    TextView mTime;

    @ViewId(R.id.weather_icon)
    ImageView mImageView;

    @ViewId(R.id.temperature)
    TextView mTemperature;

    @ViewId(R.id.description)
    TextView mDescription;

    @ViewId(R.id.more_info)
    TextView mMoreInfo;

    public HourForecastHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(WeatherForecast item, PositionInfo positionInfo) {
        L.d("Time %s", item.getTime());
        long timeStamp = item.getTimestamp();
        DayFormatter formatter = new DayFormatter(this.getContext());
        mDayOfWeek.setText(formatter.format(timeStamp));
        mTime.setText(formatter.formatTime(timeStamp));
        List<Weather> list = item.getWeathers();
        if (list != null && !list.isEmpty()) {
            Weather weather = list.get(0);
            Resources r = getContext().getResources();
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
            Picasso.with(getContext())
                    .load(Utils.getUrl(weather.getIcon()))
                    .resize(size, size)
                    .into(mImageView);
            mDescription.setText(weather.getDescription());
        }

        Main main = item.getMain();
        mTemperature.setText(Utils.formatTemperature(main.getTemperature()));

        mMoreInfo.setText(String.format("%s/%s, %s m/s, %s %%, %s hpa",
                Utils.formatTemperature(main.getTempMin()),
                Utils.formatTemperature(main.getTempMax()),
                item.getWind().getSpeed(),
                main.getHumidity(),
                main.getPressure()));
    }
}
