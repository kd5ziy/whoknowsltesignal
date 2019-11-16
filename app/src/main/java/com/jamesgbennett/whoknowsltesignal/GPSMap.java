package com.jamesgbennett.whoknowsltesignal;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class GPSMap extends FragmentActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private static final String LOG_TAG = GPSMap.class.getSimpleName();

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1111;
    private static final int REQUEST_CODE = 9999;


    private LocationCallback locationCallback;
    private LocationRequest locationRequest;


    public TextView mTextLat;
    public TextView mTextLong;
    public TextView mTextLTEStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsmap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTextLong = findViewById(R.id.text_lat_map);
        mTextLat = findViewById(R.id.text_long_map);

        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACTIVITY_RECOGNITION);

        permissionsToRequest = permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // gets location update
                    Log.d(LOG_TAG, "Fused Location updated.");
                    LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

                    mTextLat.setText(String.valueOf(location.getLatitude()));
                    mTextLong.setText(String.valueOf(location.getLongitude()));

                    mMap.addMarker(new MarkerOptions().position(currentLoc).title("You are here!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 18));
                }
            }
        };

        boolean requestingLocationUpdates = true;

        if (requestingLocationUpdates) {
            startLocationUpdates();
        }

        // get location now
        Log.d(LOG_TAG, "Fused Location updated.");
        mTextLong = findViewById(R.id.text_long_map);
        mTextLat = findViewById(R.id.text_lat_map);

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            mTextLat.setText(String.valueOf(location.getLatitude()));
                            mTextLong.setText(String.valueOf(location.getLongitude()));

                            mMap.addMarker(new MarkerOptions().position(currentLoc).title("You are here!"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 18));
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationUpdates();
    }

    private ArrayList<String> getPermissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermissions(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermissions(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PERMISSION_GRANTED;
        }

        return true;
    }

    private void startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = mMap.getMaxZoomLevel();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Add a marker locations
        LatLng sydney = new LatLng(-34, 151);
        LatLng dopplerAmazon = new LatLng(47.615083, -122.338400);

        mMap.setIndoorEnabled(true);
        mMap.addMarker(new MarkerOptions().position(dopplerAmazon).title("Marker at the Doppler Building!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dopplerAmazon, 18));

    }

    public void goToMainActivity(View view) {
        Log.d(LOG_TAG, "closing map.");

        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "Fused Location updated.");
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    public void updateLTESignalStrength(View view) {
        Log.d(LOG_TAG, "LTE Signal Strength updated.");

        mTextLTEStrength = findViewById(R.id.text_lte_signal);

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
                        REQUEST_CODE);
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
                                        Log.i(LOG_TAG, "(VERSION >= 26) rsrp: " + rsrp + ", rsrq: " + rsrq);
                                    } else {
                                        String dbm = String.valueOf(signalStrengthLte.getDbm());
                                        mTextLTEStrength.setText("dBM:" + dbm); // dbm = rsrp for values less than build 26.
                                        Log.i(LOG_TAG, String.format(Locale.getDefault(), "(VERSION < 26) rsrp: %d", dbm));
                                    }

                                    // Now get the pci.
                                    CellIdentityLte identityLte = cellInfoLte.getCellIdentity();
                                    if (identityLte != null) {
                                        //  PCI not available.
                                        //mTextLTEStrength.setText("LTE not available!");
                                        int pci = identityLte.getPci();
                                        if (pci == -1) {
                                            pci = -1;
                                        }
                                    } else {
                                        //pci = 0
                                    }

                                }
                            }
                        }
                    }
                } catch (Exception caught) {
                    Log.e(LOG_TAG, caught.getMessage(), caught);
                }

                //getting information if phone is in roaming
                boolean isRoaming = tm.isNetworkRoaming();
                //}
            }
        } catch (Exception ex) {
            Log.e("ERROR", "Error while getting signal / cell information: " + ex.getMessage());
        }
    }
}
