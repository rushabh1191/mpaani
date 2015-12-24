package com.mpaani.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;


public class InternetConnectivity extends BroadcastReceiver {

    int id=1232;
    public InternetConnectivity() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        PreferenceHelper preferenceHelper=new PreferenceHelper(context);

        if(Utility.isNetworkAvailable(context) & preferenceHelper.isUserLoggedIn()){
            sendDataToServer(context);
        }
        else {
            Utility.cancel(context,id
            );
        }

    }




    void sendDataToServer(Context context){

        DatabaseManager databaseManager=DatabaseManager.getDatabaseManger(context);
        ArrayList<String> list=databaseManager.getUnsentLocations();


        if(list.size()>0) {
            PreferenceHelper preferenceHelper=new PreferenceHelper(context);

            float distance=preferenceHelper.getFloat(PreferenceHelper.DISTANCE,0);

            // No need to calculate distance again as it is calculated every time location get updated
            Utility.showNotification("Sending " + list.size() + " location " +
                    " & Distance "+ distance + " KM    ", "Sending offline data", context, id);

        }
        else {
            Utility.cancel(context,id);
        }

        DatabaseManager.releaseDatabase();
    }
}
