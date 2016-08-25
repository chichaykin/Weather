package com.mich.weather.presenters;


import android.content.Context;
import android.content.SharedPreferences;

import com.mich.weather.R;
import com.mich.weather.data.WeatherResponse;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.services.api.location.LocationService;
import com.mich.weather.services.api.weather.WeatherServiceApi;
import com.mich.weather.utils.L;
import com.mich.weather.utils.Utils;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainPresenterImpl implements MainPresenter {
    private static final String CURRENT_CITY = "CurrentLocationName";
    private static final String CURRENT_COUNTRY = "CurrentCountryName";

    private final Context mContext;
    private final MainView mView;
    private final SharedPreferences mPreferences;
    private final LocationService mLocationService;
    private final WeatherServiceApi mWeatherServiceApi;
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private WeatherResponse mCurrentWeatherData;
    private final String mCurrentLocationName;

    @SuppressWarnings("unused")
    @Inject
    public MainPresenterImpl(Context context,
                             MainView view, SharedPreferences preferences,
                             LocationService locationService, WeatherServiceApi api) {
        mContext = context;
        mView = view;
        mPreferences = preferences;
        mLocationService = locationService;
        mWeatherServiceApi = api;
        mCurrentLocationName = context.getString(R.string.Current);
    }

    @Override
    public void onRefresh() {
        updateData(true);
    }

    @Override
    public void onActivityCreate() {
        mCompositeSubscription.add(getStoredLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<WeatherLocationPojo>>() {
                    @Override
                    public void call(List<WeatherLocationPojo> list) {
                        mView.updateSpinnerData(list);
                        // Restore current location
                        String city = mPreferences.getString(CURRENT_CITY, mCurrentLocationName);
                        String country = mPreferences.getString(CURRENT_COUNTRY, null);
                        for (WeatherLocationPojo location : list) {
                            if (location.city.equalsIgnoreCase(city)
                                    && StringUtils.equalsIgnoreCase(location.country, country)) {
                                mView.spinnerSelectLocation(location);
                                break;
                            }
                        }
                    }
                }));
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void onSpinnerClick(WeatherLocationPojo location) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(CURRENT_CITY, location.city);
        edit.putString(CURRENT_COUNTRY, location.country);
        edit.apply();

        if (mCurrentWeatherData == null
                ||!location.city.equalsIgnoreCase(mCurrentWeatherData.getCity().getName())) {
            updateData(false);
        } else {
            mView.replaceWeatherFragment();
        }
    }

    @Override
    public void addLocation(String city, String country) {
        String name = String.format("%s, %s", city.trim(), country.trim());
        WeatherLocationPojo location = new WeatherLocationPojo(city, country);
        if (!Utils.isCityExist(city, country)) {
            L.d("Adding new city %s", name);
            // save new City
            location.save();
            mView.spinnerAddLocation(location);
        } else {
            L.d("City '%s' is already existed", name);
        }

        mView.spinnerSelectLocation(location);

        updateData(false);
    }

    @Override
    public void removeLocation(WeatherLocationPojo location) {
        if (mCurrentWeatherData != null) {
            if (!isCurrentLocation(location)) {
                location.delete();
                mView.spinnerRemoveLocation(location);
            } else {
                mView.snackMessage(R.string.error_cannot_delete_current_location);
            }
        }
    }

    @Override
    public WeatherResponse getCurrentWeatherData() {
        return mCurrentWeatherData;
    }

    private void updateData(boolean resetCachedLocation) {
        mView.showProgress(true);

        mCurrentWeatherData = null;
        Observable<WeatherResponse> observable;
        WeatherLocationPojo location = mView.getCurrentLocation();
        if (isCurrentLocation(location)) {
            observable = Utils.getCurrentLocationObservable(resetCachedLocation, mLocationService, mWeatherServiceApi);
        } else {
            observable = Utils.geCityObservable(location, mWeatherServiceApi);
        }

        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeatherResponse, WeatherResponse>() {
                    @Override
                    public WeatherResponse call(WeatherResponse input) {
                        return input;
                    }
                })
                .subscribe(new SubscriberImpl())
        );
    }

    private boolean isCurrentLocation(WeatherLocationPojo location) {
        return location != null && mCurrentLocationName.equals(location.city);
    }

    private Observable<List<WeatherLocationPojo>> getStoredLocations() {
        return Observable.defer(new Func0<Observable<List<WeatherLocationPojo>>>() {
            @Override
            public Observable<List<WeatherLocationPojo>> call() {
                List<WeatherLocationPojo> list = new Select().from(WeatherLocationPojo.class).queryList();
                L.d("Stored locations: %s", list.toString());
                if (list.isEmpty()) {
                    list = createDB();
                }
                return Observable.just(list);
            }
        });
    }

    private List<WeatherLocationPojo> createDB() {
        List<WeatherLocationPojo> list = new ArrayList<>();
        //current
        WeatherLocationPojo current = new WeatherLocationPojo(mCurrentLocationName, null);
        current.insert();
        list.add(current);

        for(String city : mContext.getResources().getStringArray(R.array.default_cities)) {
            current = new WeatherLocationPojo(city, null);
            current.insert();
            list.add(current);
        }
        return list;
    }

    @SuppressWarnings("unused")
    private class SubscriberImpl extends Subscriber<WeatherResponse> {
        @Override
        public void onNext(final WeatherResponse weatherData) {
            L.d("Weather: %s", weatherData.toString());
            // Update UI with current weather.
            if (weatherData.getCity() != null) {
                mCurrentWeatherData = weatherData;
            }
            mView.showProgress(false);
        }

        @Override
        public void onCompleted() {}

        @Override
        public void onError(final Throwable error) {
            mView.showProgress(false);
            if (error instanceof TimeoutException) {
                mView.snackMessage(R.string.error_location_unavailable);
            } else {
                L.e(error.getMessage());
            }
        }
    }

}
