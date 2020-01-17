package com.illicitintelligence.techapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.illicitintelligence.techapp.R;
import com.illicitintelligence.techapp.model.PlacesResponse;
import com.illicitintelligence.techapp.network.PlacesRetrofit;

import java.security.Permission;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_CODE = 777;
    private LocationManager locationManager;

    private PlacesRetrofit placesRetrofit;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesRetrofit =  new PlacesRetrofit(getCacheDir());

        //TODO: Request Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            setUpLocation();
        else
            requestPermission();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                setUpLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void setUpLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 10, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String latlng = location.getLatitude() + "," + location.getLongitude();
        compositeDisposable.add(
                placesRetrofit.getLocationsNearby(latlng, 10000,
                        "restaraunts", "burger")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                        new Consumer<PlacesResponse>() {
                            @Override
                            public void accept(PlacesResponse placesResponse) throws Exception {


                                Gson gson = new Gson();
                                String response = gson.toJson(placesResponse);

                                Log.d("TAG_Y", response);

                                Log.d("TAG_X", "Places received : "+placesResponse.getResults().size());
                            }
                        }
                ));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        locationManager.removeUpdates(this);
    }
}
