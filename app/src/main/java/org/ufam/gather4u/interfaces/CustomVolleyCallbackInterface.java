package org.ufam.gather4u.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Raphael on 19/10/2015.
 */
public interface CustomVolleyCallbackInterface {

    /*NAO ESQUECA DE IMPLEMENTAR O METODO OnStop() COM A CHAMADA PARA MÉTODO cancelRequest NA CLASSE
    *VolleyConnectionQueue,
    * Exemplo:
         @Override
         public void onStop(){
            super.onStop();
            mVolleyConnection.canceRequest();
         }
    * ONDE mVolleyConnection É UMA INSTÂNCIA DA CLASSE VolleyConnection
    */

    //TRATA RESPOSTAS DE SUCESSO
    void deliveryResponse(JSONArray response, String flag);

    //TRATA RESPOSTAS DE SUCESSO
    void deliveryResponse(JSONObject response, String flag);

    //TRATA RESPOSTAS DE ERRO
    void deliveryError(VolleyError error, String flag);


}
