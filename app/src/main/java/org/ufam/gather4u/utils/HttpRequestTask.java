package org.ufam.gather4u.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.ufam.gather4u.application.Constants.HttpMessageType;
import org.ufam.gather4u.interfaces.CustomHttpResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestTask extends AsyncTask<Void,Void,Void>  {

    private String mURL = "";
    private JSONObject mJson = null;
    private HttpMessageType mFunction = HttpMessageType.UNDEFINED;
    private List<String> mParams = null;

    private CustomHttpResponse _listener = null;

    public HttpRequestTask(String url){
        mURL = url;
    }

    public void setURL(String url){
        this.mURL = url;
    }

    public JSONObject getJson(){
        return mJson;
    }

    public void setFunction(HttpMessageType func){ this.mFunction = func; }

    public HttpMessageType getFunction(){
        return mFunction;
    }

    public List<String> getmParams() {
        return mParams;
    }

    public void setmParams(List<String> mParams) {
        this.mParams = mParams;
    }

    public void clearParams() {
        if(this.mParams != null){
            mParams.clear();
        }
    }

    public void setListener(CustomHttpResponse listener){
        this._listener = listener;
    }


    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Showing a success message
        if (_listener != null){
            _listener.OnHttpResponse(mJson, mFunction);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            // Making a request to url and getting response
            String sep = "?";
            if (mFunction != HttpMessageType.UNDEFINED ){
                mURL += "?method=" + mFunction.getString();
                sep = "&";
            }

            if (mParams != null){
                String params = "";
                for (int i = 0; i < mParams.size(); i++){
                    params += sep + mParams.get(i);
                }
                mURL += params;
            }

            //String jsonStr = findJSONFromUrl(mURL);
            String jsonStr = doGet(mURL);
            if (!jsonStr.equalsIgnoreCase("error")){
                mJson = new JSONObject(jsonStr);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
        return null;
    }

    // Create Http connection And find Json
    public static String doPost(String url, String json) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(url);

        if (json.trim().length() > 0){
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, json);
            builder.post(body);
        }
        Request request = builder.build();

        Response response = client.newCall(request).execute();

        // restante do código
        return response.toString();

    }

    public static String doGet(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(HttpUrl.parse( url )).build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }


}
