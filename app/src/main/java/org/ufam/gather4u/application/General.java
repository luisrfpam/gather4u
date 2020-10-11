package org.ufam.gather4u.application;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.models.Avatar;
import org.ufam.gather4u.utils.BitmapHandle;

import java.util.ArrayList;

public class General {
    private static General ourInstance = new General();
    private static JSONObject loggedUser = null;
    private static Context appContext = null;
    private static Activity currActivity = null;
    private static int userType = -1;

    private static JSONArray paises = null;
    private static JSONArray estados = null;
    private static JSONArray cidades = null;
    private static JSONArray bairros = null;
    private static JSONArray regioes = null;
    private static JSONArray regiaobairros = null;  // Table regiao_bairro from DB.
    private static JSONArray bairrosRegiao = null;  // Exec join sql command.
    private static JSONArray regioesOut = null;
    private static JSONArray residuos = null;
    private static JSONArray tiporesidencia = null;

    private static JSONArray userregioestot = null;
    private static JSONArray userresiduos = null;
    private static JSONArray userregioes = null;

    private static JSONArray entregasnovas = null;
    private static JSONArray entregasaceitas = null;
    private static JSONArray entregasfinalizadas  = null;

    private static JSONArray dispentregas  = null;
    private static JSONArray residuoentregas  = null;

    private static JSONArray pontos  = null;
    private static JSONArray avaliacao  = null;

    private static JSONArray avatars  = null;

    public static ArrayList<Avatar> userAvatars  = null;

    public static int passwordMinSize = 6;

    private General() {
    }

    public static General getInstance() {
        return ourInstance;
    }

    public static JSONObject getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(JSONObject obj) {
        loggedUser = obj;
    }

    public static int getUserType() {
        return userType;
    }

    public static void setUserType(int type) {
        userType = type;
    }

    public static Context getAppContext(){
        return appContext;
    }

    public static void setAppContext(Context context){
        appContext = context;
    }

    public static void StartActivity(Intent intent){
        appContext.startActivity(intent);
    }

    public static String GenerateToken(String pValue){
        try {

            if (pValue != null){

                //String token = Sha1(MD5('gather4u' . 'admin' . 'Luiz2017Manaus' ));
                String value = "gather4u" + pValue + "Luiz2017Manaus";
                String strMD5 = new String(Hex.encodeHex(DigestUtils.md5(value) ));
                String token = new String(Hex.encodeHex(DigestUtils.sha1(strMD5)));
                return token;
            }
        }
        catch(Exception ex){

        }
        return "";
    }

    public static String GenerateToken(){
        try {

            if (loggedUser != null){
                String token = GenerateToken(loggedUser.getString("login"));
                return token;
            }
        }
        catch(Exception ex){

        }
        return "";
    }

    public static void setPaises(JSONArray value){
        paises = value;
    }

    public static JSONArray getPaises(){
        return paises;
    }

    public static void setEstados(JSONArray value){
        estados = value;
    }

    public static JSONArray getEstados(){
        return estados;
    }

    public static void setCidades(JSONArray value){
        cidades = value;
    }

    public static JSONArray getCidades(){
        return cidades;
    }

    public static void setBairros(JSONArray value){
        bairros = value;
    }

    public static JSONArray getBairros(){ return bairros; }

    public static JSONArray getRegioes() {
        return regioes;
    }

    public static void setRegioes(JSONArray regioes) {
        General.regioes = regioes;
    }
    public static JSONArray getRegiaoBairros() { return regiaobairros; }

    public static void setRegiaoBairros(JSONArray regiaobairros) {
        General.regiaobairros = regiaobairros; }

    public static JSONArray getBairrosRegiao() { return bairrosRegiao; }

    public static void setBairrosRegiao(JSONArray bairrosRegiao) {
        General.bairrosRegiao = bairrosRegiao; }

    public static JSONArray getRegioesOut() { return regioesOut; }

    public static void setRegioesOut(JSONArray regioesOut) { General.regioesOut = regioesOut; }

    public static JSONArray getResiduos() { return residuos; }

    public static void setResiduos(JSONArray residuos) { General.residuos = residuos; }

    public static JSONArray getUserResiduos() { return userresiduos; }

    public static void setUserResiduos(JSONArray userresiduos) { General.userresiduos = userresiduos; }

    public static JSONArray getUserRegioes() { return userregioes; }

    public static void setUserRegioes(JSONArray userregioes) { General.userregioes = userregioes; }

    public static JSONArray getUserRegioesTot() { return userregioestot; }

    public static void setUserRegioesTot(JSONArray userregioestot) { General.userregioestot = userregioestot; }

    public static JSONArray getTipoResidencia() { return tiporesidencia; }

    public static void setTipoResidencia(JSONArray tiporesid) { General.tiporesidencia = tiporesid; }

    /// --------------------------------------------------------------------------------------

    public static JSONArray getEntregasNovas() { return entregasnovas; }

    public static void setEntregasNovas(JSONArray entregasnovas) { General.entregasnovas = entregasnovas; }

    public static JSONArray getEntregasAceitas() { return entregasaceitas; }

    public static void setEntregasAceitas(JSONArray entregasaceitas) { General.entregasaceitas = entregasaceitas; }

    public static JSONArray getEntregasFinalizadas() { return entregasfinalizadas; }

    public static void setEntregasFinalizadas(JSONArray entregasfinalizadas) { General.entregasfinalizadas = entregasfinalizadas; }

    public static JSONArray getDispEntregas() { return dispentregas; }

    public static void setDispEntregas(JSONArray dispentregas) { General.dispentregas = dispentregas; }

    public static JSONArray getResiduoEntregas() { return residuoentregas; }

    public static void setResiduoEntregas(JSONArray residuos) { General.residuoentregas = residuos; }

    /// --------------------------------------------------------------------------------------

    public static JSONArray getPontos() { return pontos; }

    public static void setPontos(JSONArray points) { General.pontos = points; }

    public static JSONArray getAvaliacao() { return avaliacao; }

    public static void setAvaliacao(JSONArray evaluation) { General.avaliacao = evaluation; }

    public static JSONArray getAvatars() { return avatars; }

    public static void setAvatars(JSONArray _avatars) { General.avatars = _avatars; }

    /// --------------------------------------------------------------------------------------

    public static int getItemIdxByFieldValue(JSONArray list, String field, String value){
        String[] items = new String[list.length()];
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject c = list.getJSONObject(i);
                //gets the content of each tag
                if (c.getString(field).equalsIgnoreCase(value))
                {
                    return i;
                }
            }
        }
        catch (Exception ex){ }
        return -1;
    }

    public static JSONArray getItemsIdsByFieldValue(JSONArray list, String field, String value){
        JSONArray result = new JSONArray();
        try {
            String[] items = new String[list.length()];
            for (int i = 0; i < list.length(); i++) {
                JSONObject c = list.getJSONObject(i);
                //gets the content of each tag
                if (c.getString(field).equalsIgnoreCase(value))
                {
                    result.put(c);
                }
            }
        }
        catch (Exception ex){ }
        return result;
    }

    public static JSONObject getItemByFieldValue(JSONArray list, String field, String value){

        int idx = getItemIdxByFieldValue(list, field, value);
        try {
            if (idx > -1) {
                return list.getJSONObject(idx);
            }
        }
        catch (Exception ex){ }
        return null;
    }

    public static int getEstadoByUF(String value) {
        return getItemIdxByFieldValue(getEstados(), "uf", value);
    }


    public static boolean jSONContains(String value, String field, JSONArray list){

        try {
            for (int i = 0; i < list.length(); i++){
                JSONObject item = (JSONObject)list.get(i);
                if (item.has(field)) {
                    String inVal = item.getString(field);
                    if (inVal.equalsIgnoreCase(value)){
                        return true;
                    }
                }
            }
        }
        catch (Exception ex){
        }
        return false;
    }


    public static Avatar getUserAvatar(int id){

        if (userAvatars != null){
            for (int i = 0; i < userAvatars.size(); i++){

                Avatar avat = userAvatars.get(i);

                if ( avat.get_id() == id){
                    return avat;
                }
            }
        }


        return null;
    }

    public static Activity getCurrActivity() {
        return currActivity;
    }

    public static void setCurrActivity(Activity currActivity) {
        General.currActivity = currActivity;
    }

    public static void fillAvatars(){

        try {

            if (avatars != null){

                userAvatars = new ArrayList<>();
                JSONArray avats = getAvatars();

                for (int i = 0; i < avats.length(); i++ ){

                    JSONObject dbAvatar = (JSONObject) avats.get(i);

                    if (dbAvatar != null){
                        Avatar avat = Avatar.fromJSONObject(dbAvatar);
                        String strFoto = avat.getAvatar();
                        if (strFoto != null) {
                            if (strFoto.trim().length() > 0){
                                BitmapHandle imageHandler = new BitmapHandle();
                                imageHandler.loadImage(currActivity, null, avat.getLogin(), strFoto);
                            }
                        }
                        userAvatars.add(avat);
                    }
                }
            }

        }
        catch (Exception e){
        }
    }


//    public static String removeAcents(String value){
//        String newStr = Normalizer.normalize(value, Normalizer.Form.NFD);
//        return newStr.replaceAll("[^\\p{ASCII}]", "");
//    }

}
