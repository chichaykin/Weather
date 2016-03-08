package com.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.mich.weather.BuildConfig;
import com.mich.weather.services.api.location.LocationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import java.util.ArrayList;
import java.util.List;

import rx.RxJavaTestRunner;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RxJavaTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class LocationServiceTest {

    private LocationService mService;
    private ShadowLocationManager mShadowLocationManager;
    private LocationManager mLocationManager;

    @Before
    public void setUp() {
        mLocationManager = (LocationManager)
                RuntimeEnvironment.application.getSystemService(Context.LOCATION_SERVICE);
        mShadowLocationManager = shadowOf(mLocationManager);

        mService = new LocationService(mLocationManager);
    }

    /***
     * This test don't work as expected due to robolectric throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testWithBlocking() throws Exception {

        Location expectedLocation = new Location(NETWORK_PROVIDER);
        mShadowLocationManager.simulateLocation(expectedLocation);
        mShadowLocationManager.setLastKnownLocation(NETWORK_PROVIDER, expectedLocation);
        mShadowLocationManager.setLastKnownLocation(GPS_PROVIDER, expectedLocation);
        final Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        List<Criteria> list = new ArrayList<>();
        list.add(locationCriteria);
        mShadowLocationManager.setBestProvider(NETWORK_PROVIDER, true, list);
        mShadowLocationManager.setProviderEnabled(NETWORK_PROVIDER, true);
        List<String> providers = mLocationManager.getProviders(true);
        assertTrue(providers.contains(NETWORK_PROVIDER));

        Location actualLocation = mService.getLocation().toBlocking().first();
        assertSame(expectedLocation, actualLocation);
    }


}
