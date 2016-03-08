package com.mich.weather.services.api.location;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import rx.Observable;
import rx.Subscriber;

/**
 * Implement an Rx-style location service by wrapping the Android LocationManager and providing
 * the location result as an Observable.
 */
public class LocationService {
    private final LocationManager mLocationManager;
    private Location mLocation;

    public LocationService(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    public void resetLocation() {
        this.mLocation = null;
    }

    public Observable<Location> getLocation() {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(final Subscriber<? super Location> subscriber) {
                if (mLocation != null) {
                    subscriber.onNext(mLocation);
                    subscriber.onCompleted();
                } else {
                    searchCurrentLocation(subscriber);
                }
            }
        });
    }

    private void searchCurrentLocation(final Subscriber<? super Location> subscriber) {
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(final Location location) {
                mLocation = location;
                subscriber.onNext(location);
                subscriber.onCompleted();

                //noinspection ConstantConditions
                Looper.myLooper().quit();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        final Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        final String locationProvider = mLocationManager.getBestProvider(locationCriteria, true);

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        mLocationManager.requestSingleUpdate(locationProvider,
                locationListener, Looper.myLooper());

        Looper.loop();
    }
}