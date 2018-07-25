package com.jarvis.locationupdater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    static final String ACTION_PROCESS_UPDATES =
            "com.google.android.gms.location.sample.locationupdatespendingintent.action" +
                    ".PROCESS_UPDATES";

    private TextView textLat, textLong;
    public BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_PROCESS_UPDATES.equals(action)) {

                    Toast.makeText(MainActivity.this, "received to receiver",Toast.LENGTH_SHORT).show();
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        int resultCode = bundle.getInt("done");
                        if (resultCode == 1) {
                            Double latitude = bundle.getDouble("latitude");

                            Double longitude = bundle.getDouble("longitude");

                            textLat.setText(latitude.toString());
                            textLong.setText(longitude.toString());
                        }
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "onCreate",Toast.LENGTH_SHORT).show();
        textLat = (TextView) findViewById(R.id.lat);
        textLong = (TextView) findViewById(R.id.lon);


        Fragment mMapFragment = new MyMapFragmnet();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null){

            fragmentManager.beginTransaction().add(R.id.map, mMapFragment).commit();
            //fragmentManager.beginTransaction().replce(R.id.map, mMapFragment).commit();
        }


        startService(new Intent(this, MyLocationUpdatesService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(ACTION_PROCESS_UPDATES));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Resume",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Pause",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

