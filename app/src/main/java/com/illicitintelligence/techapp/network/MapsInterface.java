package com.illicitintelligence.techapp.network;

import com.illicitintelligence.techapp.model.PlacesResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MapsInterface {

    // location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=pizza&key=Q*/
    @Headers({"cache-control: public, max-age=86400"})
    @GET("/maps/api/place/nearbysearch/json")
    public Observable<PlacesResponse> getPlaceFromLocation(
            @Query("location") String latlong,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("keyword") String keyword,
            @Query("key") String Api_Key
    );

}
