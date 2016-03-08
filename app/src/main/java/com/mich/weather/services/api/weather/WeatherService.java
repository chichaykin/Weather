package com.mich.weather.services.api.weather;


import com.mich.weather.App;
import com.mich.weather.BuildConfig;
import com.mich.weather.utils.L;
import com.mich.weather.utils.ConnectivityHelper;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.File;
import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public final class WeatherService {
    private static final String BASE_URL = "http://api.openweathermap.org";
    private static final long CACHE_SIZE = 1024 * 1024 * 10;

    private WeatherService() {}

    public static WeatherServiceApi apiService() {

        final OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(AUTHORIZE_INTERCEPTOR);
        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(logging);
        }

        File fileCache = new File(App.sContext.getCacheDir(), "weather");
        //noinspection ResultOfMethodCallIgnored
        fileCache.mkdir();
        L.d("Cache: %s, exist %b", fileCache.getAbsolutePath(), fileCache.exists());
        Cache cache = new Cache(fileCache, CACHE_SIZE);
        client.setCache(cache);

        return new Retrofit.Builder().client(client)
                .baseUrl(BASE_URL)
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

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            // Add Cache Control only for GET methods
            if (request.method().equals("GET")) {
                if (!ConnectivityHelper.isNetworkAvailable()) {
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
