package com.mpaani.mpaani;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.mpaani.helpers.PreferenceHelper;
import com.mpaani.task.GetAddressFromLocation;
import com.mpaani.task.LogoutBroadcastReceiver;
import com.mpaani.task.MPaaniLocationService;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends MPaaniActivity {

    @Bind(R.id.email)
    EditText mEmailView;
    @Bind(R.id.password)
    EditText mPasswordView;

    private AddressListener mResultReceiver;

    PreferenceHelper preferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceHelper=new PreferenceHelper(this);


        ButterKnife.bind(this);


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

    }

    void detectLocationAndGetInfo(){
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        if (mLastLocation != null) {
            Intent intent=new Intent(this, GetAddressFromLocation.class);
            intent.putExtra(GetAddressFromLocation.LOCATION_DATA,mLastLocation);
            intent.putExtra(GetAddressFromLocation.RECEIVER_INFO,new AddressListener(new Handler()));
            startService(intent);
        }
    }

    public void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {


            detectLocationAndGetInfo();


        }
    }

    class AddressListener extends ResultReceiver{

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressListener(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            Address address=resultData.getParcelable("address");

//            PreferenceHelper preferenceHelper=new PreferenceHelper(this);

            preferenceHelper.saveBoolean(PreferenceHelper.IS_USER_LOGGED_IN, true);
//            preferenceHelper.saveString(PreferenceHelper.ADDRESS_OF_LOGIN, address.toString());

            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));


            finish();

            startLocationTrackingService();
            startLogoutService();


        }
    }


    void startLocationTrackingService(){
        Intent intent=new Intent(this, MPaaniLocationService.class);
        startService(intent);
    }
    void startLogoutService(){

        Calendar calendaram = Calendar.getInstance();

        calendaram.set(Calendar.HOUR_OF_DAY, 7);
        calendaram.set(Calendar.MINUTE, 0);
        calendaram.set(Calendar.SECOND, 0);
        calendaram.set(Calendar.AM_PM, Calendar.PM);



        Intent myIntent = new Intent(this, LogoutBroadcastReceiver.class);
        PendingIntent pendingIntentam = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        AlarmManager alarmManageram = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManageram.set(AlarmManager.RTC, calendaram.getTimeInMillis(), pendingIntentam);

    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }



    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }


}

