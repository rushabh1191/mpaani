package com.mpaani.mpaani;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.proxy.ProxyGrpcRequest;
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


    ProgressDialog progressBar;


    private AddressListener mResultReceiver;

    PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceHelper = new PreferenceHelper(this);


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

        checkLocationService();
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

    }

    void detectLocationAndGetInfo() {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        if (mLastLocation != null) {
            Intent intent = new Intent(this, GetAddressFromLocation.class);
            intent.putExtra(GetAddressFromLocation.LOCATION_DATA, mLastLocation);
            intent.putExtra(GetAddressFromLocation.RECEIVER_INFO, new AddressListener(new Handler()));
            startService(intent);
        }
    }


    void checkLocationService() {

        if(!isGPSEnabled(this)){
            buildAlertMessageNoGps();
        }
    }

    public static boolean isGPSEnabled(Context context){
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean isgpsEnabled= manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isgpsEnabled;

    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, You will have to enable it for continue using this app?")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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


            if(isGPSEnabled(this)) {
                preferenceHelper.saveBoolean(PreferenceHelper.IS_USER_LOGGED_IN, true);
//            preferenceHelper.saveString(PreferenceHelper.ADDRESS_OF_LOGIN, address.toString());

//            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                progressBar=ProgressDialog.show(this,"","Fetching location information");
                detectLocationAndGetInfo();
                startLogoutService();
            }
            else {
                buildAlertMessageNoGps();
            }


        }
    }

    class AddressListener extends ResultReceiver {

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

            Address address = resultData.getParcelable("address");



            preferenceHelper.saveBoolean(PreferenceHelper.IS_USER_LOGGED_IN, true);
            preferenceHelper.saveString(PreferenceHelper.ADDRESS_OF_LOGIN, address.toString());
            preferenceHelper.saveAddress(address);
            progressBar.dismiss();

            Intent intent=new Intent(LoginActivity.this,WelcomeActivity.class);
            intent.putExtra("is_coming_from_login",true);
            startActivity(intent);


            finish();



        }
    }

    void startLogoutService() {

        Calendar calendaram = Calendar.getInstance();

        calendaram.set(Calendar.HOUR_OF_DAY, 7);
        calendaram.set(Calendar.MINUTE, 0);
        calendaram.set(Calendar.SECOND, 0);
        calendaram.set(Calendar.AM_PM, Calendar.PM);


        Intent myIntent = new Intent(this, LogoutBroadcastReceiver.class);
        PendingIntent pendingIntentam = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        AlarmManager alarmManageram = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManageram.set(AlarmManager.RTC, calendaram.getTimeInMillis(), pendingIntentam);

    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }


    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }


}

