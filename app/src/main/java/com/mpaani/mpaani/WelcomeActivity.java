package com.mpaani.mpaani;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.mpaani.helpers.Logger;
import com.mpaani.helpers.PreferenceHelper;
import com.mpaani.task.LogoutBroadcastReceiver;
import com.mpaani.task.MPaaniLocationService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends MPaaniActivity {


    @Bind(R.id.tv_welcome_message)
    TextView tvMessage;


    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(logoutResponder,new IntentFilter(LogoutBroadcastReceiver.APP_LOGGED_OUT));

    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(logoutResponder);
    }

    BroadcastReceiver logoutResponder=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            logoutFromTheApp();

        }
    };

    void startLocationService(){
        startService(new Intent(this, MPaaniLocationService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        PreferenceHelper preferenceHelper=new PreferenceHelper(this);
        String address=preferenceHelper.addressOfLogin();
        String userName=preferenceHelper.userName();
        ButterKnife.bind(this);


        String text="Welcome "+userName+"</br> <b> You have logged in from </b></br>"
                +address;

        tvMessage.setText(Html.fromHtml(text));

        startLocationService();

    }


    @OnClick(R.id.btnLogout)
    void logoutFromTheApp(){

        PreferenceHelper preferenceHelper=new PreferenceHelper(this);
        preferenceHelper.saveBoolean(PreferenceHelper.IS_USER_LOGGED_IN, false);
        preferenceHelper.removeSession();
        finish();
        Intent in=new Intent(this,MPaaniLocationService.class);
        stopService(in);
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);


    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        Logger.logData("beta","Location update received");

        tvMessage.setText(location.getLatitude()+" "+location.getLongitude());
    }
}
