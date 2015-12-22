package com.mpaani.task;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.mpaani.helpers.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GetAddressFromLocation extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this

    ResultReceiver resultReceiver;


    public static String LOCATION_DATA="location_information";
    public static String RECEIVER_INFO="receiver_info";
    public GetAddressFromLocation() {
        super("GetAddressFromLocation");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Location location=intent.getExtras().getParcelable(LOCATION_DATA);
            resultReceiver=intent.getExtras().getParcelable(RECEIVER_INFO);
        List<Address> addressList=null;
        if (intent != null) {

            try {
                addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                Bundle bundle=new Bundle();
//                bundle.putParcelable("address",addressList.get(0));
                resultReceiver.send(1,bundle);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}