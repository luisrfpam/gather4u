package org.ufam.gather4u.services;

import android.net.Uri;

import com.android.volley.VolleyError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.VolleyConnection;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.interfaces.CustomVolleyCallbackInterface;

public class JsonVolleyAdapter implements CustomVolleyCallbackInterface {

    private VolleyConnection mVolleyConnection = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client = null;

    private String mFunction = "";
    private String mURL = "";
    private JSONObject mParams =  null;
    private CustomVolleyAdapterInterface listener;

    private Action viewAction = null;

    public JsonVolleyAdapter(){
        /*
        AQUI VOCE INSTANCIA A CLASSE DE CONEXAO.
        CADA ACTIVITY DEVE TER SUA INSTANCIA
        * */
        mVolleyConnection = new VolleyConnection(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client = new GoogleApiClient.Builder(General.getAppContext()).addApi(AppIndex.API).build();

        viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "APP GATHER4U", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.ufam.gather4u/http/host/path")
        );
    }

    public JsonVolleyAdapter(CustomVolleyAdapterInterface callbackListener, String method, String url, JSONObject obj)
    {
        /*
        AQUI VOCE INSTANCIA A CLASSE DE CONEXAO.
        CADA ACTIVITY DEVE TER SUA INSTANCIA
        * */
        mVolleyConnection = new VolleyConnection(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client = new GoogleApiClient.Builder(General.getAppContext()).addApi(AppIndex.API).build();

        this.mFunction = method;
        this.mParams = obj;
        this.mURL = url;
        this.listener = callbackListener;
    }

    public void setURL(String  url){
        this.mURL = url;
    }

    public void setFunction(String  method){
        this.mFunction = method;
    }

    public void setGETMethod(){
        mVolleyConnection.setGETMethod();
    }

    public void setPOSTMethod(){
        mVolleyConnection.setPOSTMethod();
    }

    public void setmParams(JSONObject obj){
        this.mParams = obj;
    }

    public void setListener(CustomVolleyAdapterInterface callbackListener){
        this.listener = callbackListener;
    }

    public void sendRequest() {
        mVolleyConnection.setClearParams(false);
        if (mParams == null){
            mParams = new JSONObject(){};
            mVolleyConnection.setClearParams(true);
        }
        mVolleyConnection.callServerApiByJsonObjectRequest(mURL, mFunction, mParams.toString(), mFunction);
    }

    //METODOS DE RETORNO
    @Override
    public void deliveryResponse(JSONObject response, String flag) {
        notifyListener(response, flag);
    }

    @Override
    public void deliveryResponse(JSONArray response, String flag) {
        //showToast("Array Resposta: " + response.toString(), true);
        //mLoginMsg.setText("Array Resposta: " + response.toString());
    }

    @Override
    public void deliveryError(VolleyError error, String flag) {
        notifyListener(error, flag);
    }

    protected void notifyListener(JSONObject response, String flag) {
        if (listener != null){
            listener.notifyListener(response, flag);
        }
    }

    protected void notifyListener(VolleyError erro, String flag) {
        if (listener != null){
            listener.notifyListener(erro, flag);
        }
    }

    public void Start()
    {
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    public void Stop(){
        AppIndex.AppIndexApi.end(client, viewAction);
        //REMOVE A REQUISINÇÃO DA FILA SE A ACTIVITY FOR FECHADA
        mVolleyConnection.canceRequest();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

}
