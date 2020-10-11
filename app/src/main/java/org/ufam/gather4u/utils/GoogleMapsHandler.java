package org.ufam.gather4u.utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ufam.gather4u.application.Constants;

public class GoogleMapsHandler implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double mLat = -1;
    private double mLng = -1;
    private LatLng mLocal = null;
    private String mTitle = "";
    private SupportMapFragment mMapFragment = null;

    public GoogleMapsHandler() {

    }

    public void setLatitue(double value){
        this.mLat = value;
    }

    public void setLongitude(double value){
        this.mLng = value;
    }

    public void zoomCamera(double lat, double lng){
        LatLng pnt = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng( pnt) );
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pnt, Constants.MAP_ZOOM));
    }

    public void setTitle(String value){
        this.mTitle = value;
    }

    public void setMapFragment(SupportMapFragment value){
        this.mMapFragment = value;
    }

    public void getMapAsync(OnMapReadyCallback activity){
        this.mMapFragment.getMapAsync(activity);
    }

    public GoogleMapsHandler(SupportMapFragment mapFragment){
        setMapFragment(mapFragment);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapLocation(mTitle, mLat, mLng);
        //mMap.setMinZoomPreference(30);
    }

    public void setMapLocation(String localName, double lat, double lng){
        try {
            this.mLat = lat;
            this.mLng = lng;
            this.mTitle = localName;
            if (mMap != null){
                mMap.setMyLocationEnabled(true);
                mMap.clear();
                mLocal = new LatLng(mLat, mLng);
                mMap.addMarker(new MarkerOptions().position(mLocal).title(mTitle));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocal));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocal, Constants.MAP_ZOOM));
            }
        }
        catch (SecurityException ex){
        }
    }

    public void setMapLocation( double lat, double lng){
        try {

            // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_endereco_hdpi);
            this.mLat = lat;
            this.mLng = lng;
            if (mMap != null){
                mMap.setMyLocationEnabled(true);
                mMap.clear();
                mLocal = new LatLng(mLat, mLng);

                MarkerOptions mo = new MarkerOptions().
                        position(mLocal).
//                        icon(icon).
                        snippet("Você está aqui!");

                mMap.addMarker(mo);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocal));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocal, Constants.MAP_ZOOM));
            }

        }
        catch (SecurityException ex){

        }

    }


}
