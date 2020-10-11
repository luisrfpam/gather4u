package org.ufam.gather4u.utils.geoHandle;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.ufam.gather4u.interfaces.MyLocationInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GeoHandle implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String TAG = this.getClass().getSimpleName();
    private Context context;
    private Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private MyLocationInterface mli;
    private String TOP_ACTIVITY;
    private LocationListener mLln;
    private Location mLocarion;
    private LocationManager mLocationManager;
    private final int MAX_UPDATE = 1;
    private int CURRENT_UPDATE;
    public static final int REQUEST_CHECK_SETTINGS= 23432;

    public GeoHandle(Context context, Activity activity, MyLocationInterface mli) {
        this.context = context;
        this.activity = activity;
        this.mli = mli;
        CURRENT_UPDATE = 0;
    }

    public Location getLocationByZipCode(String zipCode) {
        Log.w(TAG, "getLocationByZipCode: " + zipCode);
        final Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        Location location = null;
        try {
            addresses = geocoder.getFromLocationName(zipCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                location = new Location("dummyprovider");
                // Use the address as needed
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());
                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Latitude: " + address.getLatitude() + " - Longitude: " + address.getLongitude());

            } else {
                // Display appropriate message when Geocoder services are not available
                //Toast.makeText(context, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Unable to geocode zipcode");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return location;
    }

    public void connect() {
        getAndroidLocation();
    }

    public void disconnect() {
        mLocarion = null;
        if (mLln != null && mLocationManager!=null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.removeUpdates(mLln);
        }
    }

    //GOOGLE MAPS METHODS
    private synchronized void getAndroidLocation() {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.w("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        TOP_ACTIVITY = componentInfo.getClassName();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void findLocation() {
        Log.d("Find Location", "in find_location");
        String location_context = Context.LOCATION_SERVICE;
        mLocationManager = (LocationManager) context.getSystemService(location_context);
        List<String> providers = mLocationManager.getProviders(true);

        mLln = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.w(TAG, "onLocationChanged");
                Log.w(TAG, location.toString());

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if(CURRENT_UPDATE < MAX_UPDATE){
                    CURRENT_UPDATE++;
                    deliveryLocation(location);
                }

                if(mLln!=null){
                    mLocationManager.removeUpdates(mLln);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.w(TAG,"onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.w(TAG,"onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.w(TAG,"onProviderDisabled");
            }
        };

        for (String provider : providers) {
            mLocationManager.requestLocationUpdates(provider, 1000, 0,mLln);
        }
    }

    private void deliveryLocation(Location location){

        mLocarion = location;

        try{
            Log.i(TAG, mLocarion.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        mli.myLocationCallback(location);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG,"onConnected");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(l!=null){
            mLocarion = l;
        }

        if(mLocarion!=null){
            deliveryLocation(mLocarion);

        }else{
            final HashMap<String,String> error = new HashMap<>();
            if(activity!=null){
                error.put("activity",activity.getLocalClassName());
            }
            settingsrequest();
            mli.myLocationErrorCallback(error);

            Log.e(TAG,"location error");

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void settingsrequest()
    {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                Log.w(TAG,"isGpsPresent: "+state.isGpsPresent());
                Log.w(TAG,"isGpsUsable: "+state.isGpsUsable());
                Log.w(TAG,"isLocationPresent: "+state.isLocationPresent());
                Log.w(TAG,"isLocationUsable: "+state.isLocationUsable());
                Log.w(TAG,"isNetworkLocationPresent: "+state.isNetworkLocationPresent());
                Log.w(TAG,"isNetworkLocationUsable: "+state.isNetworkLocationUsable());
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Log.e(TAG,"wait for gps");
                        Log.e(TAG,"TOP "+TOP_ACTIVITY);
                        Log.e(TAG,"activity "+activity.getClass().getName());

                        findLocation();
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public static String parseAddress(Location l, Context context){
        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String fullAddr = "";

        try {
            addresses = gc.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(addresses!=null){
            for (int i = 0; i < addresses.size(); i++) {
                for (int j = 0; j <= addresses.get(i).getMaxAddressLineIndex(); j++) {
                    if(fullAddr.length()==0){
                        fullAddr = addresses.get(i).getAddressLine(j);
                    }else{
                        fullAddr = fullAddr+" "+addresses.get(i).getAddressLine(j);
                    }
                }
            }
        }

        return  fullAddr;
    }
}
