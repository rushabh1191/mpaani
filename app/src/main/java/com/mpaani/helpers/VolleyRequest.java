package com.mpaani.helpers;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.threefiveeight.adda.ApartmentAddaApp;
import com.threefiveeight.adda.Interfaces.VolleyResponseListener;
import com.threefiveeight.adda.UtilityFunctions.Logger;
import com.threefiveeight.adda.UtilityFunctions.Utilities;
import com.threefiveeight.adda.rest.api.AddaURL;
import com.threefiveeight.adda.staticmembers.StaticMembers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rushabh on 7/15/15.
 */


public class VolleyRequest {


    public static String TAG = "volley";


    private VolleyResponseListener onVolleyResponse;

    public VolleyRequest(final HashMap<String, String> params, final String url, final Context context, final int requestId, final boolean isAuthKeyToBeAdded, final VolleyResponseListener listener) {

        ApartmentAddaApp app=ApartmentAddaApp.getInstance();

        StringRequest volleyRequest=createRequest(params,url,context,requestId,isAuthKeyToBeAdded,listener);
        app.addRequest(volleyRequest,requestId+"");

    }

    public  VolleyRequest(){

    }

    public StringRequest createRequest(final HashMap<String, String> params, final String url, final Context context, final int requestId, final boolean isAuthKeyToBeAdded, final VolleyResponseListener listener) {

        onVolleyResponse = listener;
        String finalUrl;
        Logger.log("volley",url);
        if((!url.contains("http://")) & (!url.contains("https://"))) {
            finalUrl= AddaURL.makeUrl(url, context);
        }
        else
            finalUrl=url;

        Logger.log("volley", "Sending to " + finalUrl);
        StringRequest request = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (onVolleyResponse != null) {

                    Logger.log(TAG,"*************************************----------------------**********************");
                    Logger.log(TAG,"ADDA Response "+ response.trim());
                    Logger.log(TAG, "*************************************----------------------**********************");
                    onVolleyResponse.responseReceived(response, requestId);
                }
            }
        },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Logger.log("beta",error.toString());
                        if(onVolleyResponse!=null)
                            onVolleyResponse.errorReceived(error, requestId);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (isAuthKeyToBeAdded) {
                    PreferenceHelper preferenceHelper = new PreferenceHelper(context);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("auth_key", preferenceHelper.getString(StaticMembers.PREF_AUTHENTICATION_KEY));
                        json.put("owner_id", preferenceHelper.getString(StaticMembers.PREF_OWNER_ID));
                        json.put("device", "1");//1 for android
                        json.put("version", Utilities.getAppVersion(context));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    params.put("auth", Utilities.getAuthInformation(context).toString());



                }
                return params;

            }


            @Override
            public byte[] getBody() throws AuthFailureError {
                com.threefiveeight.adda.UtilityFunctions.Logger.log(TAG,"*************************************----------------------**********************");
                try {
                    com.threefiveeight.adda.UtilityFunctions.Logger.log(VolleyRequest.TAG,"SENDING ****" +getParams().toString());
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
                return super.getBody();
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }



        };


        request.setShouldCache(false);

        int socketTimeout = 30000;
        request.setRetryPolicy(new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }
}
