package com.mich.weather.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.mich.weather.App;
import com.mich.weather.R;
import com.mich.weather.data.WeatherResponse;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.utils.L;
import com.mich.weather.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


@SuppressWarnings({"WeakerAccess", "unused"})
public class MainActivity extends AppCompatActivity implements AddDialog.AddDialogListener {
    private static final String CURRENT_CITY = "CurrentLocationName";
    private static final String CURRENT_COUNTRY = "CurrentCountryName";

    @Bind(R.id.spinner)
    Spinner mSpinner;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    private CompositeSubscription mCompositeSubscription;
    private SpinnerAdapter mSpinnerAdapter;
    private WeatherResponse mCurrentWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mCompositeSubscription = new CompositeSubscription();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        mSpinnerAdapter = new SpinnerAdapter(toolbar.getContext());
        mSpinner.setAdapter(mSpinnerAdapter);

        mSpinner.setOnItemSelectedListener(new SpinnerClickListener());

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(true);
            }
        });

        mCompositeSubscription.add(App.getInstance().getStoredLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<WeatherLocationPojo>>() {
                    @Override
                    public void call(List<WeatherLocationPojo> list) {
                        // init spinner
                        mSpinnerAdapter.addAll(list);
                        mSpinnerAdapter.notifyDataSetChanged();

                        // Restore current location
                        String city = App.getInstance().getPreferences().getString(CURRENT_CITY, App.CURRENT_LOCATION_NAME);
                        String country = App.getInstance().getPreferences().getString(CURRENT_COUNTRY, null);
                        for (WeatherLocationPojo location : list) {
                            if (location.city.equalsIgnoreCase(city)
                                    && StringUtils.equalsIgnoreCase(location.country, country)) {
                                int pos = mSpinnerAdapter.getPosition(location);
                                mSpinner.setSelection(pos);
                            }
                        }
                    }
                }));
    }


    private void updateData(boolean resetCachedLocation) {
        showProgress(true);

        mCurrentWeatherData = null;
        Observable<WeatherResponse> observable;
        WeatherLocationPojo location = getCurrentLocation();
        if (isCurrentLocation(location)) {
            observable = App.getInstance().getCurrentLocationObservable(resetCachedLocation);
        } else {
            observable = App.getInstance().geCityObservable(location);
        }

        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberImpl())
        );
    }

    private WeatherLocationPojo getCurrentLocation() {
        int pos = mSpinner.getSelectedItemPosition();
        return mSpinnerAdapter.getItem(pos);
    }

    private boolean isCurrentLocation(WeatherLocationPojo location) {
        return location != null && App.CURRENT_LOCATION_NAME.equals(location.city);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_add_location:
                DialogFragment dialog = new AddDialog();
                dialog.show(getSupportFragmentManager(), "ADD");
                return true;

            case R.id.action_remove_location:
                // remove current location
                if (mCurrentWeatherData != null) {

                    WeatherLocationPojo location = (WeatherLocationPojo) mSpinner.getSelectedItem();
                    if (!isCurrentLocation(location)) {
                        location.delete();
                        mSpinnerAdapter.remove(location);
                        mSpinnerAdapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(mFab, R.string.error_cannot_delete_current_location, Snackbar.LENGTH_LONG).show();
                    }

                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onDialogPositiveClick(String city, String country) {

        String name = String.format("%s,%s", city.trim(), country.trim());
        WeatherLocationPojo location = new WeatherLocationPojo(city, country);
        if (!Utils.isCityExist(city, country)) {
            L.d("Adding new city %s", name);
            // save new City

            location.save();
            mSpinnerAdapter.add(location);
            mSpinnerAdapter.notifyDataSetChanged();
        } else {
            L.d("City '%s' is already existed", name);
        }

        int pos = mSpinnerAdapter.getPosition(location);
        mSpinner.setSelection(pos);

        updateData(false);
    }

    private void showProgress(boolean visible) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.container, visible ? ProgressFragment.newInstance() : CurrentWeatherFragment.newInstance())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    public WeatherResponse getCurrentWeatherData() {
        return mCurrentWeatherData;
    }

    private class SpinnerClickListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // When the given dropdown item is selected, show its contents in the
            // container view.

            WeatherLocationPojo location = mSpinnerAdapter.getItem(position);
            SharedPreferences.Editor edit = App.getInstance().getPreferences().edit();
            edit.putString(CURRENT_CITY, location.city);
            edit.putString(CURRENT_COUNTRY, location.country);
            edit.apply();

            if (mCurrentWeatherData == null ||
                    !location.city.equalsIgnoreCase(mCurrentWeatherData.getCity().getName())) {
                updateData(false);
            } else {

                getSupportFragmentManager().beginTransaction().replace(
                        R.id.container, CurrentWeatherFragment.newInstance())
                        .commit();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }

    private class SubscriberImpl extends Subscriber<WeatherResponse> {
        @Override
        public void onNext(final WeatherResponse weatherData) {
            L.d("Weather: %s", weatherData.toString());
            // Update UI with current weather.
            if (weatherData.getCity() != null) {
                mCurrentWeatherData = weatherData;
            }
            showProgress(false);
        }

        @Override
        public void onCompleted() {}

        @Override
        public void onError(final Throwable error) {
            showProgress(false);
            error.printStackTrace();
            if (error instanceof TimeoutException) {
                Snackbar.make(mFab, R.string.error_location_unavailable, Snackbar.LENGTH_LONG).show();
            } else {
                L.e(error.getMessage());
                error.printStackTrace();
            }
        }
    }

}