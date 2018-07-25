package com.jarvis.locationupdater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MyMapFragmnet extends Fragment  {

    static final String ACTION_PROCESS_UPDATES =
            "com.google.android.gms.location.sample.locationupdatespendingintent.action" +
                    ".PROCESS_UPDATES";

    private SupportMapFragment mapFragment;
    public static GoogleMap map;

    public static Marker geoFenceMarker;
    // Draw Geofence circle on GoogleMap
    public static Circle geoFenceLimits;
    public static ArrayList<Circle> geoFenceCircularBoundaryList;
    /************************************************Markers on Map**************************************************/
    //private Marker geoFenceMarker;
    public static ArrayList<Marker> geoFenceDrawMarkerList;


    private static final String TAG = MyMapFragmnet.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //setRetainInstance(true);
        //setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapFragment);
        fragmentTransaction.commit();

        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(locationReceiver);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Log.d(TAG, "onMapReady()");
                    map = googleMap;
                    map.setOnMapClickListener(
                            new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    Log.d(TAG, "onMapClick(" + latLng + ")");

                                    markerForGeofence(latLng);

                                }
                            }
                    );
                    map.setOnMarkerClickListener(
                            new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());

                                    return false;//because default behaviour should occur
                                    //the default behaviour is for the camera to move to the marker and an info window to appear
                                }
                            });
                }
            });
        }




        getActivity().registerReceiver(locationReceiver,
                new IntentFilter(ACTION_PROCESS_UPDATES));

    }


    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");


        String title = latLng.latitude + ", " + latLng.longitude;

        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);

        if (map != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(markerOptions);

        }
    }







    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt("done");
                if (resultCode == 1) {
                    Double latitude = bundle.getDouble("latitude");
                    Double longitude = bundle.getDouble("longitude");


                    LatLng latLng = new LatLng(latitude, longitude);
                    markerForLocation(latLng);
                }
            }
        }
    };





    private Marker locationMarker;

    private void markerForLocation(LatLng latLng) {

        Log.i(TAG, "markerLocation(" + latLng + ")");

        String title = latLng.latitude + ", " + latLng.longitude;

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);

        if (map != null) {
            if (locationMarker != null)
                locationMarker.remove();

            locationMarker = map.addMarker(markerOptions);

            float zoom = 15f;

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);

            map.animateCamera(cameraUpdate);


        }
    }
















}
