package com.mpaani.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.ArrayList;

/**
 * Created by rushabh on 24/12/15.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static int instanceCounter=0;
    private static DatabaseManager databaseInstance;

    private static String DATABASE_NAME="com.mpaani";
    private static int DATABASE_VERSION=1;

    private static SQLiteDatabase mDB;

    public static String TABLE_NAME="table_location_information";
    public static String C_LOC_INFO="location_information";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static synchronized DatabaseManager getDatabaseManger(Context context){
        if (databaseInstance == null) {
            databaseInstance = new DatabaseManager(context);
        }
        ++instanceCounter;

        return databaseInstance;
    }
    private DatabaseManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = getWritableDatabase();
    }

    public static synchronized void releaseDatabase(){
        instanceCounter--;
        if (instanceCounter < 0) {
            instanceCounter=0;
            if(mDB!=null) {
                mDB.close();
                databaseInstance.close();
                databaseInstance = null;
                mDB = null;
            }

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createOffLineLocationInfoTable(db);
    }

    public ArrayList<String> getUnsentLocations(){
        String query="SELECT "+C_LOC_INFO+" FROM "+TABLE_NAME;
        Cursor cursor=mDB.rawQuery(query,null);
        ArrayList<String> list=new ArrayList<>();
        while (cursor.moveToNext()){
            list.add(cursor.getString(0));
        }
        return  list;
    }


    public void truncateTable(){
        String query="DELETE FROM "+TABLE_NAME;
        mDB.execSQL(query);
    }
    public void addLocationInformation(Location location){
        ContentValues contentValues=new ContentValues();
        contentValues.put(C_LOC_INFO,location.getLatitude()+"|"+location.getLongitude());
        mDB.insert(TABLE_NAME,null,contentValues);
    }

    void createOffLineLocationInfoTable(SQLiteDatabase database){

        String query="CREATE TABLE "+TABLE_NAME+"("+C_LOC_INFO+TYPE_TEXT+")";
        database.execSQL(query);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
