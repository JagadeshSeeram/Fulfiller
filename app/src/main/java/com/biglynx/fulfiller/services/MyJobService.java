package com.biglynx.fulfiller.services;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends Service implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String TAG = "SyncService";
    private static final String TAG_LOC = "SyncLocation";
    Handler handler = null;
    private final long INTERVAL = 120 * 1000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //buildGoogleApiClient();
        if (handler == null)
            handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Service Started :: " + System.currentTimeMillis());
                if(mGoogleApiClient == null)
                    buildGoogleApiClient();
                sendLocationToServer();
                handler.postDelayed(this, INTERVAL);
            }
        };
        handler.postDelayed(runnable, 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        Log.e(TAG, "Stoping the service :: " + System.currentTimeMillis());
        super.onDestroy();
    }

    private void sendLocationToServer() {
        if (mCurrentLocation == null)
            return;
        HashMap<String, Object> map = new HashMap<>();
        map.put("FulfillerId", AppPreferences.getInstance(getApplicationContext()).getSignInResult() != null ?
                AppPreferences.getInstance(getApplicationContext()).getSignInResult().optString("FulfillerId") : "");
        map.put("Lat", mCurrentLocation.getLatitude());
        map.put("Long", mCurrentLocation.getLongitude());
        map.put("deviceID", getDeviceID());
        map.put("ZipCode", AppPreferences.getInstance(getApplicationContext()).getSignInResult() != null ?
                AppPreferences.getInstance(getApplicationContext()).getSignInResult().optString("ZipCode") : "");
        FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
        apiWrapper.trackUserLocationCall(AppPreferences.getInstance(getApplicationContext()).getSignInResult() != null ?
                        AppPreferences.getInstance(getApplicationContext()).getSignInResult().optString("AuthNToken") : "",
                map, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful())
                            Log.d(TAG, "API Success :: " + System.currentTimeMillis());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(TAG, "API Failure :: " + System.currentTimeMillis());
                    }
                });

    }

    private String getDeviceID() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = telephonyManager.getDeviceId();
        Log.d(TAG, "Device ID :: " + deviceID);
        return deviceID;
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrentLocation != null && location != null &&
                (mCurrentLocation.getLatitude() == location.getLatitude() &&
                        mCurrentLocation.getLongitude() == location.getLongitude())) {
            return;
        }
        mCurrentLocation = location;
        Log.d(TAG_LOC, mCurrentLocation.getLatitude() + " -- " + mCurrentLocation.getLongitude());
    }
}
