package com.blueobject.app.alive;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.helper.LocationObject;
import com.blueobject.app.alive.helper.ProtectedModel;
import com.blueobject.app.alive.helper.UserModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class GuardMapsActivity extends LocalizationActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    public SharedPreferences shared;

    private boolean started = false;
    private Handler handler = new Handler();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;


    private static final String TAG = GuardMapsActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double latitudeValue = 0.0;
    private double longitudeValue = 0.0;
    private RouteBroadCastReceiver routeReceiver;
    //private List<LocationObject> startToPresentLocations;

    public ArrayList<PModel> markers = new ArrayList<PModel>();

    LinearLayout list;
    LayoutInflater inflater;

    private class PModel extends ProtectedModel {
        public Marker marker;
        public LatLng latlng;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_guardmaps);

        new Global.StartTracking().execute();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //startToPresentLocations = mQuery.getAllLocationObjects();
        mLocationRequest = createLocationRequest();
        routeReceiver = new RouteBroadCastReceiver();


        inflater = LayoutInflater.from(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        list = (LinearLayout) findViewById(R.id.list);

        if(Global.user.guards.size() == 0) {
            TextView v = (TextView) findViewById(R.id.notfound);
            v.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        new GetPos().execute();

        startTracking();

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new GetPos().execute();
            if(started) {
                startTracking();
            }
        }
    };

    public void stopTracking() {
        started = false;
        handler.removeCallbacks(runnable);
    }

    public void startTracking() {
        stopTracking();
        started = true;
        handler.postDelayed(runnable, 30000);
    }



    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {

/*
            mMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng coordinate = new LatLng(latitude, longitude);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 16);
                //Marker marker = mMap.addMarker(new MarkerOptions().position(coordinate).icon(BitmapDescriptorFactory.fromResource(R.drawable.sospin)));
                //marker.remove();
                mMap.animateCamera(yourLocation);
            }
            */
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        enableMyLocation();
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location){
        mapObject.addMarker(new MarkerOptions().position(location).title("Current location"));
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connection method has been called");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                latitudeValue = mLastLocation.getLatitude();
                                longitudeValue = mLastLocation.getLongitude();
                                Log.d(TAG, "Latitude 4: " + latitudeValue + " Longitude 4: " + longitudeValue);
                                refreshMap(mMap);
                                markStartingLocationOnMap(mMap, new LatLng(latitudeValue, longitudeValue));
                                startPolyline(mMap, new LatLng(latitudeValue, longitudeValue));
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    private class RouteBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String local = intent.getExtras().getString("RESULT_CODE");
            assert local != null;
            if(local.equals("LOCAL")){
                //get all data from database

                /*
                //startToPresentLocations = mQuery.getAllLocationObjects();
                if(startToPresentLocations.size() > 0){
                    //prepare map drawing.
                    List<LatLng> locationPoints = getPoints(startToPresentLocations);
                    refreshMap(mMap);
                    markStartingLocationOnMap(mMap, locationPoints.get(0));
                    drawRouteOnMap(mMap, locationPoints);
                }
                */

            }
        }
    }
    private List<LatLng> getPoints(List<LocationObject> mLocations){
        List<LatLng> points = new ArrayList<LatLng>();
        /*
        for(LocationObject mLocation : mLocations){
            points.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        }
        */
        return points;
    }
    private void startPolyline(GoogleMap map, LatLng location){
        if(map == null){
            Log.d(TAG, "Map object is not null");
            return;
        }
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.add(location);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(16)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(0).latitude, positions.get(0).longitude))
                .zoom(17)
                .bearing(90)
                .tilt(40)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private void refreshMap(GoogleMap mapInstance){
        mapInstance.clear();
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    @Override
    public void onResume() {
        super.onResume();
        startTracking();
        if(routeReceiver == null){
            routeReceiver = new RouteBroadCastReceiver();
        }
        IntentFilter filter = new IntentFilter(RouteService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(routeReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopTracking();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(routeReceiver);
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void setnotfound(){
        TextView found = (TextView) findViewById(R.id.notfound);
        found.setVisibility(View.VISIBLE);
        found.setText("Nem találtunk megjeleníthető őrangyalt");
    }

    public void setfound(){
        TextView found = (TextView) findViewById(R.id.notfound);
        found.setVisibility(View.GONE);
    }


    public class GetPos extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... data) {

            String d = "";

            if(Global.user.guards.size() == 0) {
                return "";
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/getguardpos.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("myemail", Global.user.email));
                nameValuePairs.add(new BasicNameValuePair("prot", Global.gson.toJson(Global.user.guards)));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                d = reader.readLine();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return d;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("POST BACK", s);

            if(s.equals("") || s.equals("[]")) {
                setnotfound();
                list.removeAllViews();
                return;
            } else {
                setfound();
            }

            try {
                ArrayList<ProtectedModel> ps = Global.gson.fromJson(s, new TypeToken<ArrayList<ProtectedModel>>(){}.getType());
                for(PModel pm : markers) pm.marker.remove();
                markers.clear();

                if(ps.size() > 0) {
                    list.removeAllViews();
                }
                for(final ProtectedModel p : ps) {
                    Log.e("BACK P", Global.gson.toJson(p));
                    final PModel pm = new PModel();

                    if(p.lat != null && p.lng != null ) { //&& !p.lat.equals("") && !p.lng.equals("")) {

                        if(!p.lat.equals("") && !p.lng.equals("")) {
                            pm.latlng = new LatLng(Double.parseDouble(p.lat), Double.parseDouble(p.lng));
                            pm.marker = mMap.addMarker(new MarkerOptions().position(pm.latlng).title(p.name));
                            markers.add(pm);
                        }

                        RelativeLayout row = (RelativeLayout) inflater.inflate(R.layout.protected_editlist_row, null);

                        TextView v = (TextView) row.findViewById(R.id.listrow);
                        v.setText(p.name);
                        if(!p.lat.equals("") && !p.lng.equals("")) {
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pm.latlng, 16));
                                }
                            });
                        }

                        row.findViewById(R.id.editrow).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserModel g = null;
                                for(UserModel gg : Global.user.guards) {
                                    if(gg.email.equals(p.email)) {
                                        g = gg;
                                    }
                                }
                                if(g != null) {
                                    Intent dialogIntent = new Intent(Global.appContext, GuardEditActivity.class);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    dialogIntent.putExtra("new", true);
                                    dialogIntent.putExtra("guard", Global.gson.toJson(g));
                                    startActivity(dialogIntent);
                                }
                            }
                        });

                        list.addView(row);
                    }
                }

            } catch (Exception e) {
                setnotfound();
                e.printStackTrace();
            }


        }
    }

    public void exit(View v){
        finish();
    }

}
