package org.ufam.gather4u.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface CustomVolleyAdapterInterface {

    //TRATA RESPOSTAS DE SUCESSO
    void notifyListener(JSONObject response, String flag);
    void notifyListener(VolleyError erro, String flag);

}
