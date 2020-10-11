package org.ufam.gather4u.conn;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * ESSA CLASSE CONTROLA A FILA DE REQUISIÇÕES HTTP, ELA DEVE SER CHAMADA NO ONCREATE DA BASEACTIVITY
 */
public class VolleyConnectionQueue {

    private static VolleyConnectionQueue INSTANCE;
    private RequestQueue rq;

    public static synchronized VolleyConnectionQueue getINSTANCE(){
        if(INSTANCE==null){
            INSTANCE = new VolleyConnectionQueue();
        }
        return INSTANCE;
    }

    public void startQueue(Context c){
        if(rq==null){
            this.rq = Volley.newRequestQueue(c);
        }
    }

    public void addQueue(Request request){
        this.rq.add(request);
        this.rq.getCache().invalidate(request.getUrl(), true);
    }

    public void cancelRequest(String tag){
        this.rq.cancelAll(tag);
    }

}