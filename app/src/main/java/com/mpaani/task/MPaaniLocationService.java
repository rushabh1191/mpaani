package com.mpaani.task;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mpaani.helpers.Logger;
import com.mpaani.helpers.PreferenceHelper;

public class MPaaniLocationService extends Service implements   GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequester;

    final int INTERVAL = 10;
    final int FASTEST_INTERVAL = INTERVAL/2;

    final int LOCATION_TIMEOUT=15;

    boolean isRequestingUpdates=false;

    int minimumDistanceThreshold=100;

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

        locationRequester = new LocationRequest();
        locationRequester.setInterval(INTERVAL * 1000);
        locationRequester.setMaxWaitTime(FASTEST_INTERVAL * 1000);
        locationRequester.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        isRequestingUpdates = false;

        googleApiClient.connect();
        Logger.logData("beta","location service");
    }

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

        Logger.logData("beta","Api clinet connect");

        if(!isRequestingUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        if(isRequestingUpdates){
            stopLocationUpdates();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isRequestingUpdates){
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {



        Logger.logData("beta","Location changed in service "+lastLocation+" "+location);
        if(lastLocation!=null){
            float distance=location.distanceTo(lastLocation);

            PreferenceHelper preferenceHelper=new PreferenceHelper(this);
            float travelledDistance=preferenceHelper.getFloat(PreferenceHelper.DISTANCE,0);
            preferenceHelper.saveFloat(PreferenceHelper.DISTANCE,distance+travelledDistance);

            if(distance<minimumDistanceThreshold){
                //tell server that user is not moving much
            }

            Logger.logData("beta","Distance "+distance);

        }
        lastLocation=location;

        Logger.logData("beta","Last location"+lastLocation);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
