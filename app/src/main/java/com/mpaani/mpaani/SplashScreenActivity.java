package com.mpaani.mpaani;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mpaani.helpers.PreferenceHelper;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        PreferenceHelper preferenceHelper=new PreferenceHelper(this);

        Intent intent=null;
        if(preferenceHelper.isUserLoggedIn()){

            intent=new Intent(this,WelcomeActivity.class);
        }
        else {
            intent=new Intent(this,LoginActivity.class);
        }

        startActivity(intent);
        finish();

    }

}
