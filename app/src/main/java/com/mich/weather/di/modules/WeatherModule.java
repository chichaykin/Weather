package com.mich.weather.di.modules;


import android.content.Context;

import com.mich.weather.BuildConfig;
import com.mich.weather.di.scopes.ApplicationContext;
import com.mich.weather.services.api.weather.WeatherServiceApi;
import com.mich.weather.utils.ConnectivityHelper;
import com.mich.weather.utils.L;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public final class WeatherModule {
    private final String mBaseUrl;
    private final String mAppKey;
    private final long mCashSize;
    private final Context mContext;
    private final ConnectivityHelper mHelper;

    @SuppressWarnings("SameParameterValue")
    public WeatherModule(String baseUrl, String appKey, long cashSize,
                         @ApplicationContext Context context) {
        mBaseUrl = baseUrl;
        mAppKey = appKey;
        mCashSize = cashSize;
        mContext = context;
        mHelper = new ConnectivityHelper(context);
    }

    @SuppressWarnings("unused")
    @Provides
    @Singleton
    public WeatherServiceApi providesWeatherApi() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(AUTHORIZE_INTERCEPTOR)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.interceptors().add(logging);
        }

        File fileCache = new File(mContext.getCacheDir(), "weather");
        //noinspection ResultOfMethodCallIgnored
        fileCache.mkdir();
        L.d("Cache: %s, exist %b", fileCache.getAbsolutePath(), fileCache.exists());
        Cache cache = new Cache(fileCache, mCashSize);
        clientBuilder.cache(cache);

        return new Retrofit.Builder().client(clientBuilder.build())
                .baseUrl(mBaseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(WeatherServiceApi.class);

    }

    private final Interceptor AUTHORIZE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            HttpUrl url = request.url().newBuilder().addQueryParameter("appid", mAppKey).build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        }
    };

    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            // Add Cache Control only for GET methods
            if (request.method().equals("GET")) {
                if (!mHelper.isNetworkAvailable()) {
                    // 4 weeks stale
                    request = request.newBuilder()
                            .header("Cache-Control", "public, max-stale=2419200")
                            .build();
                }
            }
            Response originalResponse = chain.proceed(request);
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=86400")
                    .build();
        }
    };
}
