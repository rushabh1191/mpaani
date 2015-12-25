package com.mpaani.task;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mpaani.helpers.DatabaseManager;
import com.mpaani.helpers.Logger;
import com.mpaani.helpers.PreferenceHelper;
import com.mpaani.helpers.Utility;
import com.mpaani.mpaani.LoginActivity;

public class MPaaniLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequester;

    final int INTERVAL_IN_MINUTE = 2;
    final int FASTEST_INTERVAL = INTERVAL_IN_MINUTE / 2;


    boolean isRequestingUpdates = false;

    int minimumDistanceThresholdInKm = 1;

    Location lastLocation;

    public MPaaniLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        registerReceiver
                (gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        locationRequester = new LocationRequest();
        locationRequester.setInterval(INTERVAL_IN_MINUTE *60* 1000);
        locationRequester.setFastestInterval(FASTEST_INTERVAL *60* 1000);
        locationRequester.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        isRequestingUpdates = false;

        googleApiClient.connect();
        Logger.logData("beta", "location service");
    }

    BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!LoginActivity.isGPSEnabled(MPaaniLocationService.this)) {
                Utility.showNotification("Please enable GPS", "Error", MPaaniLocationService.this, 983);
            } else {

                Utility.cancel(context, 9803);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates
                (googleApiClient,
                        locationRequester, this);
        isRequestingUpdates = true;

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        isRequestingUpdates = false;
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (!isRequestingUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        if (isRequestingUpdates) {
            stopLocationUpdates();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRequestingUpdates) {
            googleApiClient.disconnect();
        }

        unregisterReceiver(gpsReceiver);
    }

    @Override
    public void onLocationChanged(Location location) {


        if (lastLocation != null) {

            float distance = location.distanceTo(lastLocation);

            PreferenceHelper preferenceHelper = new PreferenceHelper(this);
            float travelledDistance = preferenceHelper.getFloat(PreferenceHelper.DISTANCE, 0);


            travelledDistance += distance;

            preferenceHelper.saveFloat(PreferenceHelper.DISTANCE, travelledDistance);

            if (Utility.isNetworkAvailable(this)) {
                if (distance < minimumDistanceThresholdInKm) {
                    Utility.showNotification("User is not travelling ", "MPaani Alert", this);
                } else {
                    Utility.showNotification("Sending Info Distance :" + getFormattedKM(travelledDistance / 1000) + " KM", "MPaani Information", this);
                }
            } else {
                Utility.showNotification("No internet,App send when internet will be available", "No Network", this, 10);

                DatabaseManager databaseManager = DatabaseManager.getDatabaseManger(this);
                databaseManager.addLocationInformation(location);
                DatabaseManager.releaseDatabase();
            }

        }
        lastLocation = location;
    }




    public String getFormattedKM(float number) {
        return String.format("%.2f", number);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
