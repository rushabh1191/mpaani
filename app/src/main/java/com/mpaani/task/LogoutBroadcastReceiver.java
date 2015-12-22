package com.mpaani.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mpaani.helpers.Logger;
import com.mpaani.helpers.PreferenceHelper;

public class LogoutBroadcastReceiver extends BroadcastReceiver {

    public static final String APP_LOGGED_OUT="app_logged_out";
    public LogoutBroadcastReceiver() {


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        PreferenceHelper preferenceHelper=new PreferenceHelper(context);
        preferenceHelper.saveBoolean(PreferenceHelper.IS_USER_LOGGED_IN,false);
        context.stopService(new Intent(context,MPaaniLocationService.class));
        Logger.logData("beta", "Logging out user");

        context.sendBroadcast(new Intent(APP_LOGGED_OUT));
    }
}
