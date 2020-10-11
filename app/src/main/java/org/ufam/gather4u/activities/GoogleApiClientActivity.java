package org.ufam.gather4u.activities;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.squareup.picasso.Picasso;

import org.ufam.gather4u.models.User;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.activities.Permitions.listeners.GpsPermitionListener;
import org.ufam.gather4u.activities.alert.AlertsListActivity;
import org.ufam.gather4u.activities.event.EventsListActivity;

import java.io.File;

public class GoogleApiClientActivity extends RequestPermitionsActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener {
    private static final String TAG = UserDataActivity.class.getSimpleName();
    private TextView txtLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationReq;
    private boolean isNewUser = false;
    private User mUser;
    private ImageView mIvAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_api_client);

        txtLocation = (EditText) findViewById(R.id.txt_location);
        mIvAccount = (ImageView) findViewById(R.id.iv_account);

        if(getIntent().getExtras()!=null){
            isNewUser = getIntent().getBooleanExtra("is_new_user",false);
        }
        ActionBar actBar = getSupportActionBar();

        if(!isNewUser){

            findViewById(R.id.btn_account).setVisibility(View.GONE);
            findViewById(R.id.btn_events).setOnClickListener(this);
            findViewById(R.id.btn_alerts).setOnClickListener(this);
            findViewById(R.id.btn_logout).setOnClickListener(this);
            findViewById(R.id.btn_location).setOnClickListener(this);


            if(actBar!=null){
                actBar.setSubtitle(getString(R.string.title_activity_google_api_client));
            }

            mUser = getIntent().getParcelableExtra("user");


            String path = mUser.getAvatar();

            if(path!=null){
                final File myImageFile = new File(path);
                Picasso.with(this)
                        .load(myImageFile)
                        .into(mIvAccount);
            }
        }else{
            if(actBar!=null){
                actBar.setDisplayHomeAsUpEnabled(true);
            }
            findViewById(R.id.bottom_menu_user_data).setVisibility(View.GONE);
        }

        hideKeyboard();
        callConnection();
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
            }

            @Override
            public void onGpsDenied() {
                Log.i("LOG", "Gps não permitido.");
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Log.i("LOG", "OnConnected: " + bundle);

            Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (l != null) {
                l.setAccuracy( 6.0F );
                Log.i("LOG", "Latitude: " + l.getLatitude());
                Log.i("LOG", "Longitude: " + l.getLongitude());
            }
            txtLocation.setText(l.getLatitude() + " | " + l.getLongitude());
            startLocationUptade();
        }
        catch(SecurityException se)
        {
            Log.i("LOG", "onConnected erro: " + se.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initLocationRequest()
    {
        mLocationReq = new LocationRequest();
        mLocationReq.setInterval(5000);
        mLocationReq.setFastestInterval(2000);
        mLocationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUptade()
    {
        try {
            initLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReq, GoogleApiClientActivity.this );
        }
        catch (SecurityException se)
        {
            Log.i("LOG", "Update Location erro: " + se.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        txtLocation.setText(
                location.getLatitude() + " | " + location.getLongitude() +
                        " - Altitude: " + location.getAltitude() +
                        " - Acuracy: " + location.getAccuracy()  );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended erro: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult != null){
            Log.i("LOG", "onConnectionFailed erro: " + connectionResult.getErrorMessage());
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.btn_alerts:
                intent = new Intent(this, AlertsListActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;

            case R.id.btn_events:
                intent = new Intent(this, EventsListActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;

            case R.id.btn_logout:
                performLogout();
                break;
        }
    }

}
