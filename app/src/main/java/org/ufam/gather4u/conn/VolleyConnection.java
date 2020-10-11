package org.ufam.gather4u.conn;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.interfaces.CustomVolleyCallbackInterface;
import org.ufam.gather4u.services.CustomJsonArrayRequest;
import org.ufam.gather4u.services.CustomJsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

public class VolleyConnection {

    private CustomVolleyCallbackInterface mCustomVolleyCallbackInterface;
    private Map<String, String> params;
    private String mVOLLEYTAG;
    private int mMethod = Request.Method.POST;
    private Boolean noParams = false;

    public VolleyConnection(CustomVolleyCallbackInterface cvci){
        VolleyConnectionQueue.getINSTANCE(); //inicia a fila de requisições
        mCustomVolleyCallbackInterface = cvci;
    }

    protected void setVolleyTag(String tag){
        Log.i("APP", "setVolleyTag(" + tag + ")");
        this.mVOLLEYTAG = tag;
    }

    public void canceRequest(){
        if(mVOLLEYTAG!=null){
            VolleyConnectionQueue.getINSTANCE().cancelRequest(this.mVOLLEYTAG);
        }
    }

    public void setGETMethod() {
        this.mMethod = Request.Method.GET;
    }

    public void setPOSTMethod() {
        this.mMethod = Request.Method.POST;
    }

    public void setClearParams(Boolean value) {
        this.noParams = value;
    }


    //METODO PARA ENVIO E RECEBIMENTO DE JSONARRAYS
    public void callServerApiByJsonArrayRequest(final String url, String method, String data, final String flag){

        // Se a propriedade for false então manda os parâmetros
        if (!noParams){
            params = new HashMap<String, String>();
            if(data!=null){
                params.put("data", data);
            }
            if(method!=null){
                params.put("method", method);
            }
        }

        final String activityName = mCustomVolleyCallbackInterface.getClass().getSimpleName();
        Log.i("SEND MESSAGE","["+activityName+"] url: "+url+" data: "+data);

        CustomJsonArrayRequest request = new CustomJsonArrayRequest(mMethod,
                url,
                params,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        //envia a resposta de sucesso para a activity
                        mCustomVolleyCallbackInterface.deliveryResponse(response,flag);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //envia a mensagem de erro para a activity
                        mCustomVolleyCallbackInterface.deliveryError(error,flag);
                    }
                });

        request.setTag(activityName);
        this.setVolleyTag(activityName);
        //rq.add(request);
        VolleyConnectionQueue.getINSTANCE().addQueue(request);
    }

    //METODO PARA ENVIO E RECEBIMENTO DE JSONOBJECTS
    public void callServerApiByJsonObjectRequest(final String url, String method, String value, final String flag){

        Log.i("PHOTO_ACTIVITY", "ENTREI: callByJsonObjectRequest()");
        if (!noParams) {
            if (value != null) {
                //params.put("data", value);
                try {
                    if (value.indexOf('{') == -1) {
                        value = "{'data':'" + value + "'}";
                    }
                    params = new HashMap<String, String>();
                    params.put("data", new JSONObject(value).toString());
                    params.put("method", method);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        final String activityName = mCustomVolleyCallbackInterface.getClass().getSimpleName();
        Log.i("SEND MESSAGE","["+activityName+"] url: "+url+" data: "+value);

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(mMethod,
                url,
                params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        //envia a resposta de sucesso para a activity
                        mCustomVolleyCallbackInterface.deliveryResponse(response, flag);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //envia a mensagem de erro para a activity
                        mCustomVolleyCallbackInterface.deliveryError(error, flag);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("tagPhotoActivity");
        this.setVolleyTag("tagPhotoActivity");
        //rq.add(request);
        VolleyConnectionQueue.getINSTANCE().addQueue(request);
    }

}
