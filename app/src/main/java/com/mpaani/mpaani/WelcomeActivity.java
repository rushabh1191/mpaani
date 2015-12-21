package com.mpaani.mpaani;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.mpaani.helpers.PreferenceHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {


    @Bind(R.id.tv_welcome_message)
    TextView tvmessage;
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

        tvmessage.setText(Html.fromHtml(text));


    }
}
