package org.ufam.gather4u.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ufam.gather4u.activities.Permitions.listeners.GpsPermitionListener;
import org.ufam.gather4u.interfaces.CustomMapInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GPSLocation extends ContextWrapper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        android.location.LocationListener,
        OnMapReadyCallback,
        CustomMapInterface {

    private GoogleMapsHandler map = null;
    private GoogleMap mGMap = null;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationReq = null;
    private GpsPermitionListener mGpsPermitionListener = null;
    private SupportMapFragment mMapFragmet = null;
    private LocationManager mLocationManager;
    private LocationListener listener = null;
    private Activity mActivity = null;

    private CustomMapInterface locListener = null;

    private ArrayList markerPoints = null;
    private MarkerOptions mMarkerOptions = null;
    private LatLng defaultPoint = null;
    private DownloadTask downloadTask = null;

    private Boolean mCanClick = true;
    private Boolean mUseGPS = true;
    private Boolean mGranted = false;

    private String mDistance;
    private String mDuration;

    public static final int ACCESS_FINE_LOCATION_PERMITION = 3;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private Double lat = 0.0;
    private Double lng = 0.0;

    public GPSLocation(Context base, Activity activity){
        super(base);
        setActivity(activity);
        Start();
    }

    public void Start() {
        callConnection();
        mGoogleApiClient.connect();
    }

    public void Stop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void setListener(LocationListener list) {
        this.listener = list;
    }

    public void setFragment(SupportMapFragment mapFragment){
        this.mMapFragmet = mapFragment;
        map = new GoogleMapsHandler(mMapFragmet);
        mMapFragmet.getMapAsync(this);
    }

    public boolean isGranted(){
        return mGranted;
    }


    private synchronized void callConnection(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        checkGpsPermition(new GpsPermitionListener() {

            @Override
            public void onGpsGranted() {

                mGoogleApiClient.connect();
                mGranted = true;
            }

            @Override
            public void onGpsDenied() {
                Log.i("LOG", "Gps não permitido.");
                mGranted = false;
            }
        });

        mMarkerOptions = new MarkerOptions();
        markerPoints = new ArrayList();
    }

    private void checkGpsPermition(GpsPermitionListener listener) {
        checkGpsPermition(getActivity(), listener );
    }

    protected void checkGpsPermition(Activity activity, GpsPermitionListener gpsPermitionListener){

        this.mGpsPermitionListener = gpsPermitionListener;

        if (activity != null){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMITION);
                } else {
                    mGpsPermitionListener.onGpsGranted();
                }
            } else {
                mGpsPermitionListener.onGpsGranted();
            }
        }
        else
        {
            Log.i("LOG", "checkGpsPermition erro: Propriedade 'activity' not set");
        }


    }

    private void initLocationRequest()
    {
        mLocationReq = new LocationRequest();
        mLocationReq.setInterval(5000);
        mLocationReq.setFastestInterval(2000);
        mLocationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUptade()
    {
        try {
            initLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReq, this );
            try {
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            }
            catch (SecurityException ex){
                mGranted = false;
            }
        }
        catch (SecurityException se)
        {
            Log.i("LOG", "Update Location erro: " + se.getMessage());
            mGranted = false;
        }
    }

    public Boolean getCanClick() { return mCanClick; }

    public void setCanClick(Boolean canClick) { this.mCanClick = canClick; }

    public double getLatitude(){
        return this.lat;
    }

    public double getLongitude(){
        return this.lng;
    }

    public void setLatitude(double _lat){ this.lat = _lat; }

    public void setLongitude(double _lng){ this.lng = _lng; }

    public Boolean getUseGPS() { return mUseGPS; }

    public void setUseGPS(Boolean mUseGPS) { this.mUseGPS = mUseGPS; }

    public void setLocation(double latitude, double longitude){
        this.lat = latitude;
        this.lng = longitude;
        if (map != null){
            map.setMapLocation(lat, lng);
        }
    }

    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            mGranted = false;
            mGranted = false;
        }
        return null;
    }

    private void markDefaultPoint(){
        markerPoints.clear();
        checkGpsPermition( mGpsPermitionListener);
        if (mGMap != null){
            mGMap.clear();
        }
        setLocation(defaultPoint.latitude, defaultPoint.longitude);
        markerPoints.add(defaultPoint);
        mMarkerOptions.position(defaultPoint);
        mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    public Address getAddress(String endereco)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocationName(endereco, 1);
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            mGranted = false;
        }
        return null;
    }

    public void AddPoint(LatLng point){

        String all_vals = String.valueOf(point);
        String[] separated = all_vals.split(":");
        String latlng[] = separated[1].split(",");

        double lat = Double.parseDouble(latlng[0].trim().substring(1));
        double lon = Double.parseDouble(latlng[1].substring(0, latlng[1].length() - 1));

        setLatitude(lat);
        setLongitude(lon);
        map.setLatitue(lat);
        map.setLongitude(lon);

        // Adding new item to the ArrayList
        markerPoints.add(point);
        mMarkerOptions.position(point);

        if (markerPoints.size() == 2) {
            mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
//                  else if (markerPoints.size() == 1) {
//                        mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                    }
        mGMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        mGMap.addMarker(mMarkerOptions);
        mMarkerOptions.title("Local Entrega");

        checkDrawRoute();
        //map.zoomCamera(lat, lon);
    }

    public void checkDrawRoute()
    {
        if (markerPoints.size() >= 2) {
            LatLng origin = (LatLng) markerPoints.get(0);
            LatLng dest = (LatLng) markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);
            downloadTask = new DownloadTask(mGMap);

            downloadTask.setListener(locListener);

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            map.zoomCamera(dest.latitude, dest.longitude );
//
//            if (locListener != null){
//                if (downloadTask != null) {
//                    locListener.setDistance(downloadTask.getDistance());
//                    locListener.setDuration(downloadTask.getDuration());
//                }
//            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Log.i("LOG", "OnConnected: " + bundle);

            Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (l != null) {
                l.setAccuracy( 6.0F );
                Log.i("LOG", "Latitude: " + l.getLatitude());
                Log.i("LOG", "Longitude: " + l.getLongitude());

                lat = l.getLatitude();
                lng = l.getLongitude();

                // Adding new item to the ArrayList
                defaultPoint = new LatLng(lat, lng);
                markDefaultPoint();
//                setLocation( l.getLatitude(), l.getLongitude() );
            }
        }
        catch( SecurityException | NullPointerException se){
            Log.i("LOG", "onConnected erro: " + se.getMessage());
            mGranted = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended erro: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed erro: " + connectionResult.getErrorMessage());
        mGranted = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        if (map != null) {
            map.setMapLocation(lat, lng);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        mGranted = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGMap = googleMap;
        setUpMap();
    }

    public void setUpMap() {
        try {
            mGMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGMap.setMyLocationEnabled(true);
            //mGMap.setTrafficEnabled(true);
            mGMap.setIndoorEnabled(true);
            mGMap.getCameraPosition();
            mGMap.setBuildingsEnabled(true);
            mGMap.getUiSettings().setZoomControlsEnabled(true);

            mMarkerOptions.title("Local Entrega");

            mGMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {

                    if (markerPoints.size() >= 2) {
                        markerPoints.clear();
                        mGMap.clear();
                        markDefaultPoint();
                    }

                    if (getCanClick()) {
                        AddPoint(point);
                    }

                    if (listener != null) {
                        listener.onLocationChanged(mGMap.getMyLocation());
                    }
                }
            });
        }
        catch (SecurityException ex){
            mGranted = false;
        }
    }

    public GoogleMap getGMap() { return mGMap; }

    public void setGMap(GoogleMap mGMap) { this.mGMap = mGMap; }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public CustomMapInterface getLocListener() { return locListener; }

    public void setLocListener(CustomMapInterface locListener) { this.locListener = locListener; }

    public Activity getActivity() { return mActivity; }

    public void setActivity(Activity mActivity) { this.mActivity = mActivity; }
}
