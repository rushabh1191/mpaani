package com.mpaani.mpaani;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.mpaani.helpers.Logger;
import com.mpaani.helpers.PreferenceHelper;
import com.mpaani.task.LogoutBroadcastReceiver;
import com.mpaani.task.MPaaniLocationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends MPaaniActivity {


    @Bind(R.id.tv_welcome_message)
    TextView tvMessage;

    @Bind(R.id.tv_time)
    TextView tvTime;

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

        Bundle bundle=getIntent().getExtras();
        tvTime.setVisibility(View.GONE);
        if(bundle!=null) {

            if (bundle.containsKey("is_coming_from_login")) {
                Date date = new Date();

                tvTime.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy HH:mm");
                String time = sdf.format(date); // formats to 09/23/2009 13:53:28.238

                tvTime.setText("You have looged in at "+time);

            }
        }


        String text="Welcome <h3>"+userName+"</h3><br/> <b> You have logged in from </b><br/>"
                +preferenceHelper.getAddress().replace("\n","<br/>");

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


    }
}
