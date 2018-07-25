package com.jarvis.locationupdater;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyLocationUpdatesService extends Service implements LocationListener

{

    private static final String TAG = MyLocationUpdatesService.class.getSimpleName();
    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 sIcNnds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.
    private static final String PENDING_INTENT_ACTION_CONSTANT_FOR_INTENT_SERVICE_FROM_LOCATION_SERVICE
            = "com.google.android.gms.location.sample.locationupdatespendingintent.action" + ".PROCESS_UPDATES";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private PendingIntent mLocationPendingIntent;

    @Override
    public IBinder onBind(@Nullable Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Toast.makeText(this, "Service onCreate", Toast.LENGTH_SHORT).show();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

    }
/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
        return START_REDELIVER_INTENT;
        //return START_NOT_STICKY;
    }
*/
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(this, "onStart Service", Toast.LENGTH_SHORT).show();
        startLocationUpdates();


    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private PendingIntent getLocationPendingIntent() {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        // TODO(developer): uncomment to use PendingIntent.getService().
//        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
//        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (mLocationPendingIntent != null) {

            return mLocationPendingIntent;
        } else {

            Intent intent = new Intent(this, LocationUpdatesIntentService.class);
            intent.setAction(PENDING_INTENT_ACTION_CONSTANT_FOR_INTENT_SERVICE_FROM_LOCATION_SERVICE);
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }
    }


    // Start location Updates
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        Toast.makeText(this, "startLocationUpdates()", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getLocationPendingIntent());
        } else {
            //Request Permissions
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Found : "
                + location.getLatitude() + ", "
                + location.getLongitude() + ". "
                + location.getAccuracy());

        broadcastLocationFoundToTextViewOfMain(location);
        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
    }


    public void broadcastLocationFoundToTextViewOfMain(Location location) {
        Intent intent = new Intent(PENDING_INTENT_ACTION_CONSTANT_FOR_INTENT_SERVICE_FROM_LOCATION_SERVICE);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        intent.putExtra("done", 1);

        sendBroadcast(intent);
    }


    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(getLocationPendingIntent());
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();

    }
}
