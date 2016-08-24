package com.mich.weather.di.modules;


import android.content.Context;

import com.mich.weather.BuildConfig;
import com.mich.weather.services.api.weather.WeatherServiceApi;
import com.mich.weather.utils.ConnectivityHelper;
import com.mich.weather.utils.L;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

@Module
public final class WeatherModule {

    private final String mBaseUrl;
    private final long mCashSize;
    private final ConnectivityHelper mHelper;

    public WeatherModule(String baseUrl, long cashSize, Context context) {
        mBaseUrl = baseUrl;
        mCashSize = cashSize;
        mHelper = new ConnectivityHelper(context);
    }

    @Provides
    @Singleton
    public WeatherServiceApi provideWeatherApi(Context context) {

        final OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(AUTHORIZE_INTERCEPTOR);
        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(logging);
        }

        File fileCache = new File(context.getCacheDir(), "weather");
        //noinspection ResultOfMethodCallIgnored
        fileCache.mkdir();
        L.d("Cache: %s, exist %b", fileCache.getAbsolutePath(), fileCache.exists());
        Cache cache = new Cache(fileCache, mCashSize);
        client.setCache(cache);

        return new Retrofit.Builder().client(client)
                .baseUrl(mBaseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(WeatherServiceApi.class);

    }

    private static final Interceptor AUTHORIZE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            HttpUrl url = request.httpUrl().newBuilder().addQueryParameter("appid","f5a59b6b24b40224fd8d6a69f74f6c98").build();
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
