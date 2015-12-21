package com.mpaani.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rushabh on 5/9/14.
 */
public class PreferenceHelper {


    private static final String USER_NAME = "user_name";
    private SharedPreferences preference;

    private final String PREFERENCE_NAME="com.mpaani.app";

    public static final String IS_USER_LOGGED_IN="is_user_logged_in";
    public static final String ADDRESS_OF_LOGIN ="login_address";
    public PreferenceHelper(Context context){
        try {
            preference=context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        catch (Exception e)
        {

        }
    }

    public boolean isUserLoggedIn(){
        return  preference.getBoolean(IS_USER_LOGGED_IN,false);
    }

    public String addressOfLogin(){
        return  preference.getString(ADDRESS_OF_LOGIN, "Mumbai");
    }

    public String userName(){

        return preference.getString(USER_NAME,"'");
    }


    public void removeSession()
    {
        SharedPreferences.Editor editor = preference.edit();
        editor.clear();
        editor.apply();
    }


    public void saveInt(String key,int value)
    {
        preference.edit().putInt(key,value).apply();

    }

    public int getInt(String key,int returnValue)
    {
        return preference.getInt(key,returnValue);
    }

    public int getInt(String key)
    {
        return  preference.getInt(key, -1);
    }

    /*Saving & getting string from preference*/
    public void saveString(String key,String value)
    {
        preference.edit().putString(key,value).apply();
    }

    public String getString(String key,String returnVale)
    {
        return preference.getString(key,returnVale);
    }

    public String getString(String key)
    {
        return  preference.getString(key, null);
    }

    /*saving getting boolean*/

    public void saveBoolean(String key,boolean value)
    {
        preference.edit().putBoolean(key,value).apply();
    }

    public boolean getBoolean(String key)
    {
        return preference.getBoolean(key, false);
    }
    public boolean getBoolean(String key,boolean value)
    {
        return preference.getBoolean(key,value);
    }

    /*getting and saving float*/

    public void saveFloat(String key,float value)
    {
        preference.edit().putFloat(key,value).apply();
    }

    public float getFloat(String key,float returnValue)
    {
        return preference.getFloat(key, returnValue);
    }
    public float getFloat(String key)
    {
        return  preference.getFloat(key, -1.0f);
    }

    public boolean contains(String key){
        return preference.contains(key);
    }


}
