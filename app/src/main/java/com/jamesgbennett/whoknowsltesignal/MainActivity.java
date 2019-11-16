package com.jamesgbennett.whoknowsltesignal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public EditText mTextLong;
    public EditText mTextLat;
    public EditText mTextLTEStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextLong = findViewById(R.id.text_long_map);
        mTextLat = findViewById(R.id.text_lat_map);
        mTextLTEStrength = findViewById(R.id.text_lte_strength);

    }

    public void openMapMode(View view) {
        Log.d(LOG_TAG,"open map clicked.");
        Intent mapIntent = new Intent(this, GPSMap.class);

        startActivity(mapIntent);
    }



    public void openMode3(View view) {
        Log.d(LOG_TAG,"open mode3 clicked.");
        Intent mode3 = new Intent(this, Mode3.class);

        startActivity(mode3);
    }

    public void openMode2(View view) {
        Log.d(LOG_TAG,"open mode 2 clicked.");
        Intent mode2 = new Intent(this, Mode2.class);

        startActivity(mode2);
    }

    public void openDebugSensors(View view) {
        Log.d(LOG_TAG, "open debug sensors");
        Intent debugSensors = new Intent(this, SensorTest.class);

        startActivity(debugSensors);
    }
}
