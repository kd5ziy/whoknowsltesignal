package com.jamesgbennett.whoknowsltesignal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Looper;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

//implements GoogleApiClient.ConnectionCallbacks,
  //      GoogleApiClient.OnConnectionFailedListener,

public class SensorTest extends AppCompatActivity
        implements LocationListener,
                SensorEventListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public EditText mTextLong;
    public EditText mTextLat;
    public EditText mTextLTEStrength;
    public EditText mTextStepCount;
    public EditText mTextStepBatchDelay;
    public EditText mTextOrientationX;
    public EditText mTextOrientationY;
    public EditText mTextOrientationZ;
    public EditText mTextMagSensorData;

    public EditText mTextMagReading;
    public EditText mTextOrientationReading;


    static final int PCI_NA = -1;


    private int mSteps = 0;
    private int mCounterSteps = 0;
    private int mPreviousCounterSteps = 0;
    private int mDelay = 0;

    private static final int EVENT_QUEUE_LENGTH = 10;
    private float[] mEventDelays = new float[EVENT_QUEUE_LENGTH];

    private int mEventLength = 0;
    private int mEventData = 0;

    private FusedLocationProviderClient mFusedLocationProviderClient;


    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Sensor orientationSensor;

    private Sensor magSensor;

    private Sensor mMagetometerSensor;
    private Sensor mAccelerometerSensor;




    //private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    final float[] rotationMatrix = new float[9];
    final float[] orientationAngles = new float[3];

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);

        mTextLong = findViewById(R.id.text_long_map);
        mTextLat = findViewById(R.id.text_lat_map);

        mTextStepCount = findViewById(R.id.text_step_count);

        mTextMagSensorData = findViewById(R.id.text_mag_sensor_data);

        mTextMagReading = findViewById(R.id.text_mag_reading);
        mTextOrientationReading = findViewById(R.id.text_orientation_reading);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACTIVITY_RECOGNITION);

        permissionsToRequest = permissions;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsToRequest.size() > 0){
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        mAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        /*googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();*/

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult == null){
                    return;
                }
                for (Location location : locationResult.getLocations()){
                    // gets location update
                    Log.d(LOG_TAG,"Fused Location updated.");

                    mTextLat.setText(String.valueOf(location.getLatitude()));
                    mTextLong.setText(String.valueOf(location.getLongitude()));
                }
            }
        };

        boolean requestingLocationUpdates = true;

        if(requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private ArrayList<String> getPermissionsToRequest(ArrayList<String> wantedPermissions){
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions){
            if(!hasPermissions(perm)){
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermissions(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return checkSelfPermission(permission) == PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public void onStart(){
        super.onStart();

        /*if(googleApiClient != null){
            googleApiClient.connect();
        }*/
    }

    @Override
    public void onResume(){
        super.onResume();

        //registerSensorListener(Sensor.TYPE_ACCELEROMETER);
        registerSensorListener(Sensor.TYPE_MAGNETIC_FIELD);
        //registerSensorListener(Sensor.TYPE_AMBIENT_TEMPERATURE);
        registerSensorListener(Sensor.TYPE_LIGHT);

        //mMagetometerSensor = sensorManager.registerListener(this, mMagetometerSensor, SensorManager.SENSOR_DELAY_GAME);
        //mAccelerometerSensor = sensorManager.registerListener(this, mMagetometerSensor, SensorManager.SENSOR_DELAY_GAME);


        //if(!checkPlayServices()){
            //tell user to fix this!
        //}

        boolean requestingLocationUpdates = true;

        if(requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void registerSensorListener(int type) {
        Sensor sensor = sensorManager.getDefaultSensor(type);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        unregisterListeners();

        stopLocationUpdates();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        //outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
        //        requestLocationUpdates);

        super.onSaveInstanceState(outState);
    }

    private void startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates(){
        //if(googleApiClient != null && googleApiClient.isConnected()){
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        //}
    }

    /*private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if(resultCode != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            }else{
                finish();
            }

            return false;
        }
        return true;
    }

     */

    private void unregisterListeners() {
        SensorManager sensorManager =
                (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.unregisterListener(mListener);
        sensorManager.unregisterListener(this);

        //log
    }

    // Location data from Fused Location Provider

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG,"Fused Location updated.");
        mTextLong = findViewById(R.id.text_long_map);
        mTextLat = findViewById(R.id.text_lat_map);

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mTextLat.setText(String.valueOf(location.getLatitude()));
                            mTextLong.setText(String.valueOf(location.getLongitude()));
                        }
                    }
                });

    }

    // end location from fused provider


    // Step data
    private final SensorEventListener mListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            EditText mTextCounterSteps = findViewById(R.id.text_step_count);


            recordDelay(event);
            final String delayString = getDelayString();

            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                mSteps += event.values.length;

                // set mCounterSteps
                // set text field!!
                mTextCounterSteps.setText("Total Steps: " + String.valueOf(mSteps));

                //log something here
                Log.i("STEP_INFO", "Got new step info from the step counter!");

            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                if (mCounterSteps < 1){
                    mCounterSteps = (int) event.values[0];
                }

                mSteps = (int) event.values[0] - mCounterSteps;

                mSteps += mPreviousCounterSteps;

                // set mCounterSteps
                // set text field!!
                // mTextCounterSteps = "Total Steps: {mSteps}";

                mTextCounterSteps.setText("Total Steps: " + String.valueOf(mSteps));


                Log.i("STEP_INFO", "Got new step info from the step counter!");
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void recordDelay(SensorEvent event){
        mEventDelays[mEventData] = System.currentTimeMillis() - (event.timestamp / 1000000L);

        mEventLength = Math.min(EVENT_QUEUE_LENGTH, mEventLength + 1);
        mEventData = (mEventData + 1) % EVENT_QUEUE_LENGTH;
    }

    private final StringBuffer mDelayStringBuffer = new StringBuffer();

    private String getDelayString(){
        mDelayStringBuffer.setLength(0);

        for(int i = 0; i < mEventLength; i++){
            if(i>0){
                mDelayStringBuffer.append(", ");
            }

            final int index = (mEventData + i) % EVENT_QUEUE_LENGTH;
            final float delay = mEventDelays[index] / 1000f;
            mDelayStringBuffer.append(String.format("%1.1f", delay));
        }

        return mDelayStringBuffer.toString();
    }

    public void registerStepCounter(View view) {
        mTextStepCount = findViewById(R.id.text_step_count);
        mTextStepBatchDelay = findViewById(R.id.text_batch_delay);

        mDelay = Integer.valueOf(mTextStepBatchDelay.getText().toString());

        if(mDelay > 0){
            mDelay = mDelay * 1000000;
        } else{
            Log.d("Delay error", "Error getting delay integer!");
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        final boolean batchMode = sensorManager.registerListener(mListener, stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL, mDelay);

    }

    public void onSensorChanged(SensorEvent event){
        EditText mTextCounterSteps = findViewById(R.id.text_step_count);


        recordDelay(event);
        final String delayString = getDelayString();

        /*if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            mSteps += event.values.length;

            // set mCounterSteps
            // set text field!!
            mTextCounterSteps.setText("Total Steps: " + String.valueOf(mCounterSteps));

            //log something here
            Log.i("STEP_INFO", "Got new step info from the step counter!");

        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            if (mCounterSteps < 1){
                mCounterSteps = (int) event.values[0];
            }

            mSteps = (int) event.values[0] - mCounterSteps;

            mSteps += mPreviousCounterSteps;

            // set mCounterSteps
            // set text field!!
            // mTextCounterSteps = "Total Steps: {mSteps}";

            mTextCounterSteps.setText("Total Steps: " + String.valueOf(mSteps));


            Log.i("STEP_INFO", "Got new step info from the step counter!");
        } else */
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //Write output!
            //save accelerometer output!

            //System.arraycopy(event.values, 0, accelerometerReading,
            //        0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //Write output!
            // save magnetic field output!

            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);


        } else if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            //save temperature!
        } else if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            //save light data!
        }
    }

    public void updateOrientationAngles(){
        mTextMagReading = findViewById(R.id.text_mag_reading);
        mTextOrientationReading = findViewById(R.id.text_orientation_reading);

        //SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading,
        //       mAccelerometerSensor);

        //SensorManager.getOrientation(rotationMatrix, mMagetometerSensor);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //end step data

    //Location Methods

    public void updateGPSLocation(View view) {
        Log.d(LOG_TAG,"GPS Location updated.");
        mTextLong = findViewById(R.id.text_long_map);
        mTextLat = findViewById(R.id.text_lat_map);

        //Location location = new Location();
        LatLng currentLocation = this.getLocation();

        if(currentLocation != null) {
            mTextLat.setText(String.valueOf(currentLocation.latitude));
            mTextLong.setText(String.valueOf(currentLocation.longitude));
        }


    }

    public void getFusedLocation(View view){

        Log.d(LOG_TAG,"Fused Location updated.");
        mTextLong = findViewById(R.id.text_long_map);
        mTextLat = findViewById(R.id.text_lat_map);

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mTextLat.setText(String.valueOf(location.getLatitude()));
                            mTextLong.setText(String.valueOf(location.getLongitude()));
                        }
                    }
                });
    }


    private LocationManager locationManager;
    private Criteria criteria;

    private static int MY_LOCATION_REQUEST_CODE = 329;

    public LatLng getLocation(){
        LatLng currentLocation = null;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setSpeedRequired(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE);
        } else {
            currentLocation = getLocationPrivate();
        }
        return currentLocation;
    }

    @SuppressLint("MissingPermission")
    private LatLng getLocationPrivate() {
        LatLng location = null;
        String bestProvider = locationManager.getBestProvider(criteria, true);

        android.location.Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
        if (currentLocation != null) {
            location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            // lets set a default location... something went wrong!!!
            Log.d("Location.getLocation", "Error while getting location!!!");
        }

        return location;
    }

    //End Location Methods

    // Signal Strength
    static final int EXECELLENT_RSRP_THRESHOLD = -95;
    static final int GOOD_RSRP_THRESHOLD = -110;
    static final int POOR_RSRP_THRESHOLD = -140;

    static final String EXECELLENT_RSRP_COLOR  = "#00ff00";
    static final String GOOD_RSRP_COLOR  = "#ffff00";
    static final String POOR_RSRP_COLOR  = "#ff0000";



    private static int MY_PHONE_REQUEST_CODE = 123;

    private final String TAG = this.getClass().getSimpleName();

    @SuppressLint("MissingPermission")
    public void updateLTESignalStrength(View view) {
        Log.d(LOG_TAG, "LTE Signal Strength updated.");

        mTextLTEStrength = findViewById(R.id.text_lte_strength);

        try {
            //Get the instance of TelephonyManager
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE
                        },
                        MY_PHONE_REQUEST_CODE);
            } else {
                //Calling the methods of TelephonyManager the returns the information

                // GETTING IMEI number (device id is restricted on android 10 Q)
                //String IMEINumber = tm.getDeviceId();
                //String subscriberID = tm.getDeviceId();
                //String SIMSerialNumber = tm.getSimSerialNumber();
                //String networkCountryISO = tm.getNetworkCountryIso();
                //String SIMCountryISO = tm.getSimCountryIso();
                //String softwareVersion = tm.getDeviceSoftwareVersion();
                //String voiceMailNumber = tm.getVoiceMailNumber();

                //Get the phone type
                String strphoneType = "";

                int phoneType = tm.getPhoneType();

                switch (phoneType) {
                    case (TelephonyManager.PHONE_TYPE_CDMA):
                        strphoneType = "CDMA";
                        break;
                    case (TelephonyManager.PHONE_TYPE_GSM):
                        strphoneType = "GSM";
                        break;
                    case (TelephonyManager.PHONE_TYPE_NONE):
                        strphoneType = "NONE";
                        break;
                }

                int networkType = tm.getNetworkType();

                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
                        if (allCellInfo != null) {
                            for (int i = 0; i < allCellInfo.size(); i++) {
                                if (allCellInfo.get(i).isRegistered() && allCellInfo.get(i) instanceof CellInfoLte) {
                                    CellInfoLte cellInfoLte = (CellInfoLte) allCellInfo.get(i);
                                    CellSignalStrengthLte signalStrengthLte = cellInfoLte.getCellSignalStrength();

                                    // Overwrite the SignalStrengthListener values if build is greater than 26 and only the rsrp if value less than 26.
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        String rsrp = String.valueOf(signalStrengthLte.getRsrp());
                                        String rsrq = String.valueOf(signalStrengthLte.getRsrq());
                                        mTextLTEStrength.setText("RSRP: " + rsrp);
                                        Log.i(TAG, "(VERSION >= 26) rsrp: " + rsrp + ", rsrq: " + rsrq);
                                    } else {
                                        String dbm = String.valueOf(signalStrengthLte.getDbm());
                                        mTextLTEStrength.setText("dBM:" + dbm); // dbm = rsrp for values less than build 26.
                                        Log.i(TAG, String.format(Locale.getDefault(), "(VERSION < 26) rsrp: %d", dbm));
                                    }

                                    // Now get the pci.
                                    CellIdentityLte identityLte = cellInfoLte.getCellIdentity();
                                    if (identityLte != null) {
                                        //  PCI not available.
                                        //mTextLTEStrength.setText("LTE not available!");
                                        int pci = identityLte.getPci();
                                        if (pci == PCI_NA){
                                            pci = -1;
                                        }

                                        //dataReadingCopy.setPci(pci);
                                    } else{
                                        //pci = 0
                                    }

                                }
                            }
                        }
                    }
                } catch (Exception caught) {
                    Log.e(TAG, caught.getMessage(), caught);
                }

                //getting information if phone is in roaming
                boolean isRoaming = tm.isNetworkRoaming();
                //}
            }
        } catch(Exception ex){
            Log.e("ERROR", "Error while getting signal / cell information: " + ex.getMessage());
        }
    }


    public void registerOrientation(View view) {
        mTextOrientationX = findViewById(R.id.text_orientation_x);
        mTextOrientationY = findViewById(R.id.text_orientation_y);
        mTextOrientationZ = findViewById(R.id.text_orientation_z);


        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading,
                magnetometerReading);


        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);

        mTextOrientationX.setText(String.valueOf(orientation[0]));
        mTextOrientationY.setText(String.valueOf(orientation[1]));
        mTextOrientationZ.setText(String.valueOf(orientation[2]));


        float[] test = orientation;
    }


    public void getMagSensorData(View view) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //magSensor.



    }
}
