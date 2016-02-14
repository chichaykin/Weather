package com.mich.weather.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mich.weather.App;
import com.mich.weather.R;
import com.mich.weather.data.Coord;
import com.mich.weather.data.WeatherLocation;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends AppCompatActivity implements AddDialog.AddDialogListener {

    private static final String CURRENT_NAME = "NAME";
    private static final long LOCATION_TIMEOUT_SECONDS = 20;
    private View mProgressBar;
    private CompositeSubscription mCompositeSubscription;
    private SpinnerAdapter mAdapter;
    private String mCurrentName;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Spinner mSpinner;
    private WeatherLocation mWeatherData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCompositeSubscription = new CompositeSubscription();

        mProgressBar = findViewById(R.id.progress_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mCurrentName = App.getInstance().getPreferences().getString(CURRENT_NAME, WeatherLocationPojo.CURRENT_LOCATION_NAME);
        mAdapter = new SpinnerAdapter(toolbar.getContext(), new String[]{WeatherLocationPojo.CURRENT_LOCATION_NAME});
        mSpinner.setAdapter(mAdapter);

        mSpinner.setOnItemSelectedListener(new SpinnerClickListener());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        LocationListener listener = new LocationListener();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(listener)
                .build();

        updateData();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void updateData() {
        mProgressBar.setVisibility(View.VISIBLE);

        Observable<WeatherLocation> fetchDataObservable = App.getInstance().getLocations()
                .filter(new Func1<WeatherLocationPojo, Boolean>() {
                    @Override
                    public Boolean call(WeatherLocationPojo pojo) {
                        return pojo.Name.equals(mCurrentName);
                    }
                })
                //.timeout(LOCATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .map(new Func1<WeatherLocationPojo, Coord>() {
                    @Override
                    public Coord call(WeatherLocationPojo pojo) {
                        return App.getInstance().build(pojo, mLastLocation);
                    }
                })
                .flatMap(new Func1<Coord, Observable<WeatherLocation>>() {
                    @Override
                    public Observable<WeatherLocation> call(Coord coordinates) {
                        return App.getInstance().getWeatherService().getCurrentWeather(
                                coordinates.getLon(), coordinates.getLat());
                    }
                });

        mCompositeSubscription.add(fetchDataObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberImpl())
        );
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
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String city, String country) {
        dialog.dismiss();
        showProgress(true);
    }

    private void showProgress(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    public WeatherLocation getCurrentWeatherData() {
        return mWeatherData;
    }

    private class SpinnerClickListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // When the given dropdown item is selected, show its contents in the
            // container view.
            WeatherFragment fragment = WeatherFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class SubscriberImpl extends Subscriber<WeatherLocation> {
        @Override
        public void onNext(final WeatherLocation weatherData) {
            L.d("Weather: %s", weatherData.toString());
            // Update UI with current weather.
            mWeatherData = weatherData;

            List<String> list = new ArrayList<>();
            for(WeatherLocationPojo p : App.getInstance().getLocationList()) {
                list.add(p.Name);
            }

            mAdapter = new SpinnerAdapter(MainActivity.this, list.toArray(new String[list.size()]));
            mSpinner.setAdapter(mAdapter);
        }

        @Override
        public void onCompleted() {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(final Throwable error) {
            mProgressBar.setVisibility(View.GONE);
            error.printStackTrace();
            if (error instanceof TimeoutException) {
                Snackbar.make(mProgressBar, R.string.error_location_unavailable, Snackbar.LENGTH_LONG).show();
            } else {
                L.e(error.getMessage());
                error.printStackTrace();
                throw new RuntimeException("See inner exception");
            }
        }
    }

    private class LocationListener implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle connectionHint) {
            if (ActivityCompat.checkSelfPermission(App.sContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(App.sContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }
    }
}