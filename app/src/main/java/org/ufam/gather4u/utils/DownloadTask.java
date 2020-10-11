package org.ufam.gather4u.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import org.ufam.gather4u.interfaces.CustomMapInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, String, String>  {

    private GoogleMap mMap;
    private CustomMapInterface listener = null;

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public GoogleMap getmMap() { return mMap; }

    public void setmMap(GoogleMap mMap) { this.mMap = mMap; }

    public DownloadTask( GoogleMap map){
        this.mMap = map;
    }

    public CustomMapInterface getListener() { return listener; }

    public void setListener(CustomMapInterface listener) { this.listener = listener; }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ParserTask parserTask = new ParserTask();
        parserTask.setmMap(this.mMap);
        parserTask.setListener(listener);
        parserTask.execute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... url) {

        String data = "";

        try {
            data = downloadUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }



}
