package org.ufam.gather4u.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.activities.Permitions.listeners.GpsPermitionListener;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.GoogleMapsHandler;

public class UserLocationActivity extends RequestPermitionsActivity implements
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CustomVolleyAdapterInterface { //implements OnMapReadyCallback {

    private GoogleMapsHandler map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationReq;
    private Gather_User mGatherUser = null;
    private TextView mLatitude = null;
    private TextView mLongitude = null;
    private Button mBtnSave = null;
    private JsonVolleyAdapter mVlAdapt = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION =   "updlocation";
    public static final String KEY_TOKEN    =   "token";
    public static final String KEY_USER     =   "user";

    public static final int DLG_WAITTING_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        mLongitude = (TextView) findViewById(R.id.input_longitude);
        mLatitude = (TextView) findViewById(R.id.input_latitude);
        mBtnSave = (Button) findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = new GoogleMapsHandler(mapFragment);
        //mapFragment.getMapAsync(this);
        mVlAdapt = new JsonVolleyAdapter();

        mGatherUser =  Gather_User.fromJSONObject(General.getLoggedUser());

        if( actBar != null){
            actBar = getSupportActionBar();
        }
        actBar.setDisplayHomeAsUpEnabled(true);

        if( isGPSAvailable() ){
            callConnection();
        }
        else
        {
            msgToastError(this, "Ative o GPS para continuar", true );
        }
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
                mLatitude.setText(String.valueOf(l.getLatitude()));
                mLongitude.setText(String.valueOf(l.getLongitude()));
            }
            map.setMapLocation(l.getLatitude(), l.getLongitude());
            startLocationUptade();
        }
        catch( SecurityException | NullPointerException se){
            Log.i("LOG", "onConnected erro: " + se.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mVlAdapt.Start();
    }

    @Override
    public void onStop() {
        mVlAdapt.Stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMsgDialog == null){

            if (isGPSAvailable()){
                callConnection();
            }

            if (!isNetworkAvailable()){
                msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null){
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReq, this );
        }
        catch (SecurityException se)
        {
            Log.i("LOG", "Update Location erro: " + se.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        mLatitude.setText(String.valueOf(lat));
        mLongitude.setText(String.valueOf(lng));
        map.setMapLocation(lat, lng);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended erro: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed erro: " + connectionResult.getErrorMessage());
    }

    @Override
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if (response != null){
            try {

                dismissLoadingDialog();

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("erro")){
                    msgError("Erro na atualização do usuário");
                }
                else if (jResp.has("data")){

                    if (jResp.getString("data").contains("100")){
                        mGoogleApiClient.disconnect();
                        msgToastOk(this, "Atualização realizada com sucesso", true);
                    }
                }
            }
            catch (Exception ex){
                msgError("Erro: " + ex.toString());
            }
        }
    }

    @Override
    public void notifyListener(VolleyError erro, String flag) {
        Log.i("Script", "Erro: " + erro);
        dismissLoadingDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                SaveLocation();
                break;
        }
    }

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.UPD_USUARIOS_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
    }

    private void SaveLocation() {

        try {

            if (mGatherUser != null){

                showLoadingDialog();

                mGatherUser.setLatitude( mLatitude.getText().toString() );
                mGatherUser.setLongitude(mLongitude.getText().toString());

                JSONObject jUser = new JSONObject();
                String strToken = General.GenerateToken(mGatherUser.getLogin());
                jUser.put(KEY_TOKEN, strToken);
                jUser.put(KEY_USER, mGatherUser.toString());

                setSaveDadosVolleyAdapter();
                mVlAdapt.setmParams(jUser);
                mVlAdapt.sendRequest();
            }
        }
        catch (Exception ex){
            msgError("Erro: " + ex.toString());
        }
    }
}
