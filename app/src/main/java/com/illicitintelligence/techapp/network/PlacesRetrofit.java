package com.illicitintelligence.techapp.network;


import android.util.Log;

import com.illicitintelligence.techapp.model.PlacesResponse;
import com.illicitintelligence.techapp.util.Constants;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesRetrofit {

    private final String BASE_URL = "https://maps.googleapis.com";

    private MapsInterface mapsInterface;

    private OkHttpClient okHttpClient;

    private long cacheSize = 10 * 1024 * 1024;

    private Cache cache;

    public PlacesRetrofit(File cacheDirectory) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {



                Log.d("TAG_X", message);
            }
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        cache =  new Cache(cacheDirectory, cacheSize);

        okHttpClient = new OkHttpClient().newBuilder()
                .cache(cache)
                .addInterceptor(logging)
                .build();

        mapsInterface = create(getInstance());
    }

        private Retrofit getInstance () {
            return new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .build();
        }

        private MapsInterface create (Retrofit retrofit){
            return retrofit.create(MapsInterface.class);
        }

        public Observable<PlacesResponse> getLocationsNearby (
                String latlong,
        int radius,
        String type,
        String keyword
    ){
            return mapsInterface.getPlaceFromLocation(latlong, radius, type, keyword, Constants.API_KEY);
        }
    }
