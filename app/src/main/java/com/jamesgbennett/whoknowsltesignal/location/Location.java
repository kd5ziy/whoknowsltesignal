package com.jamesgbennett.whoknowsltesignal.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class Location extends AppCompatActivity {
    private LocationManager locationManager;
    private Criteria criteria;

    private static int MY_LOCATION_REQUEST_CODE = 329;

    public LatLng getLocation(){
        LatLng currentLocation = null;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
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
}
