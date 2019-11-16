
package com.jamesgbennett.whoknowsltesignal.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "dbWhoKnowsSignalStrength";

    // DB Table Name
    private static final String TABLE_NAME = "tblSignalStrength";

    ///DB Columns
    private static final String SIGNALSTRENGTHID = "SignalStrengthId";
    private static final String LTESIGNAL = "LTESignal";
    private static final String CDMASignal = "CDMASignal";
    private static final String GSMSignal = "GSMSignal";
    private static final String TIME = "Time";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String ALTITUDE = "Altitude";
    private static final String RSRP = "rsrp";
    private static final String RSRQ = "rsrq";
    private static final String DBM = "dbm";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SIGNALSTRENGTHID + " INT PRIMARY KEY not null unique," +
                    LTESIGNAL + "INT not null," +
                    CDMASignal + "INT not null," +
                    GSMSignal + "INT not null," +
                    TIME + " TEXT not null," +
                    LATITUDE + " TEXT not null," +
                    LONGITUDE + " TEXT not null," +
                    ALTITUDE + " TEXT not null," +
                    RSRP + " REAL not null," +
                    RSRQ + " REAL not null," +
                    DBM  + " REAL not null)";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase database;


    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
    }


    public ArrayList<LocationData> getLocationData() {

        // Load countries - return ArrayList of loaded countries
        Log.d(TAG, "loadCountries: START");
        ArrayList<LocationData> locationData = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SIGNALSTRENGTHID, LTESIGNAL, CDMASignal, GSMSignal, TIME, LATITUDE, LONGITUDE, ALTITUDE, RSRP, RSRQ, DBM}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                TIME); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                Integer sSignalStrengthId = cursor.getInt(0);
                String sLTESignal = cursor.getString(1);
                String sCDMASignal = cursor.getString(2);
                String sGSMSignal = cursor.getString(3);
                String sTIME = cursor.getString(4);
                String sLATITUDE = cursor.getString(5);
                String sLONGITUDE = cursor.getString(6);
                String sALTITUDE = cursor.getString(7);
                String sRSRP = cursor.getString(8);
                String sRSRQ = cursor.getString(9);
                String sDBM = cursor.getString(10);
                locationData.add(new LocationData(sSignalStrengthId, sLTESignal, sCDMASignal,
                        sGSMSignal, sTIME, sLATITUDE, sLONGITUDE, sALTITUDE, sRSRP, sRSRQ, sDBM));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadLocationData: DONE");

        return locationData;
    }

    public ArrayList<LocationData> getLocationDataSinceDateTime(Date date) {

        // Load countries - return ArrayList of loaded countries
        Log.d(TAG, "loadCountries: START");
        ArrayList<LocationData> locationData = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SIGNALSTRENGTHID, LTESIGNAL, CDMASignal, GSMSignal, TIME, LATITUDE, LONGITUDE, ALTITUDE, RSRP, RSRQ, DBM}, // The columns to return
                TIME + ">=" + date, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                TIME); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                Integer sSignalStrengthId = cursor.getInt(0);
                String sLTESignal = cursor.getString(1);
                String sCDMASignal = cursor.getString(2);
                String sGSMSignal = cursor.getString(3);
                String sTIME = cursor.getString(4);
                String sLATITUDE = cursor.getString(5);
                String sLONGITUDE = cursor.getString(6);
                String sALTITUDE = cursor.getString(7);
                String sRSRP = cursor.getString(8);
                String sRSRQ = cursor.getString(9);
                String sDBM = cursor.getString(10);
                locationData.add(new LocationData(sSignalStrengthId, sLTESignal, sCDMASignal,
                        sGSMSignal, sTIME, sLATITUDE, sLONGITUDE, sALTITUDE, sRSRP, sRSRQ, sDBM));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadLocationData: DONE");

        return locationData;
    }

    public void ResetDatabase(){
    }

    void addLocationData(LocationData locationData) {
        ContentValues values = new ContentValues();


        values.put(LTESIGNAL, locationData.LTESIGNAL);
        values.put(CDMASignal, locationData.CDMASignal);
        values.put(GSMSignal, locationData.GSMSignal);
        values.put(TIME, locationData.TIME);
        values.put(LATITUDE, locationData.LATITUDE);
        values.put(LONGITUDE, locationData.LONGITUDE);
        values.put(ALTITUDE, locationData.ALTITUDE);
        values.put(RSRP, locationData.RSRP);
        values.put(RSRQ, locationData.RSRQ);
        values.put(DBM, locationData.DBM);

        database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addLocationData: Add Complete");
        Log.d(TAG, "addLocationData: " + locationData.RSRP);
        Log.d(TAG, "addLocationData: " + locationData.TIME);
        Log.d(TAG, "addLocationData: " + locationData.LATITUDE);
        Log.d(TAG, "addLocationData: " + locationData.LONGITUDE);


    }



    void updateStock(LocationData locationData) {
        ContentValues values = new ContentValues();

        values.put(LTESIGNAL, locationData.LTESIGNAL);
        values.put(CDMASignal, locationData.CDMASignal);
        values.put(GSMSignal, locationData.GSMSignal);
        values.put(TIME, locationData.TIME);
        values.put(LATITUDE, locationData.LATITUDE);
        values.put(LONGITUDE, locationData.LONGITUDE);
        values.put(ALTITUDE, locationData.ALTITUDE);
        values.put(RSRP, locationData.RSRP);
        values.put(RSRQ, locationData.RSRQ);
        values.put(DBM, locationData.DBM);

        database.update(TABLE_NAME, values, SIGNALSTRENGTHID + " = ?", new String[]{locationData.SIGNALSTRENGTHID.toString()});

        Log.d(TAG, "updateLocatoinData: Update Complete");
    }

    void deleteStock(Integer signalStrengthId) {
        Log.d(TAG, "deleteLocationData: Deleting LocationData" + signalStrengthId);

        int cnt = database.delete(TABLE_NAME, SIGNALSTRENGTHID + " = ?", new String[]{signalStrengthId.toString()});

        Log.d(TAG, "deleteLocationData:" + cnt);
    }



    void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String sSignalStrengthId = cursor.getString(0);
                String sLTESignal = cursor.getString(1);
                String sCDMASignal = cursor.getString(2);
                String sGSMSignal = cursor.getString(3);
                String sTIME = cursor.getString(4);
                String sLATITUDE = cursor.getString(5);
                String sLONGITUDE = cursor.getString(6);
                String sALTITUDE = cursor.getString(7);
                String sRSRP = cursor.getString(8);
                String sRSRQ = cursor.getString(9);
                String sDBM = cursor.getString(10);


                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", SIGNALSTRENGTHID + ":", sSignalStrengthId) +
                        String.format("%s %-18s", LTESIGNAL + ":", sLTESignal) +
                        String.format("%s %-18s", CDMASignal + ":", sCDMASignal) +
                        String.format("%s %-18s", GSMSignal + ":", sGSMSignal) +
                        String.format("%s %-18s", TIME + ":", sTIME) +
                        String.format("%s %-18s", LATITUDE + ":", sLATITUDE) +
                        String.format("%s %-18s", LONGITUDE + ":", sLONGITUDE) +
                        String.format("%s %-18s", ALTITUDE + ":", sALTITUDE) +
                        String.format("%s %-18s", RSRP + ":", sRSRP) +
                        String.format("%s %-18s", RSRQ + ":", sRSRQ) +
                        String.format("%s %-18s", DBM + ":", sDBM));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    void shutDown() {
        database.close();
    }
}
