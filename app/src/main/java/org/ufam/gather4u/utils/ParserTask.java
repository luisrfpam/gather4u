package org.ufam.gather4u.utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.ufam.gather4u.interfaces.CustomMapInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

    private GoogleMap mMap;

    private JSONObject mLegs = null;

    private CustomMapInterface listener;

    public CustomMapInterface getListener() { return listener; }

    public void setListener(CustomMapInterface listener) { this.listener = listener; }

    public GoogleMap getmMap() { return mMap; }

    public void setmMap(GoogleMap mMap) { this.mMap = mMap; }

    private String getLegValue(String tagName){
        try {
            if (mLegs != null){

                String dist = ((JSONObject)mLegs.getJSONObject(tagName)).getString("text");

                return dist;
            }
        }
        catch (Exception ex)
        {
        }
        return "";
    }

    public String getDistance(){
        return getLegValue("distance");
    }

    public String getDurationTime(){
        return getLegValue("duration");
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String,String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String,String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            routes = parser.parse(jObject);
            mLegs = parser.getLeg();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String,String>>> result) {
        ArrayList points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        if (listener != null){
            new Handler().post(new Runnable() {
                public void run() {
                    listener.setDistance( getDistance() );
                    listener.setDuration( getDurationTime() );
                }
            });
        }

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap<String,String>> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap point = path.get(j);

                double lat = Double.parseDouble(point.get("lat").toString());
                double lng = Double.parseDouble(point.get("lng").toString());
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);
        }
        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null){
            getmMap().addPolyline(lineOptions);
        }
    }
}
