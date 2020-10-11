package org.ufam.gather4u.utils;

import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.Constants.HttpMessageType;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.CustomHttpResponse;

import java.util.ArrayList;
import java.util.List;

public class GatherTables implements CustomHttpResponse{

    private Handler timerHandler = null;
    private String ID = "id";
    private String IDPAIS = "idpais";
    private String IDBAIRRO = "idbairro";
    private String IDESTADO = "idestado";
    private String IDCIDADE = "idcidade";
    private String IDREGIAO = "idregiao";
    private String IDRESIDUO = "idresiduo";
    private String IDREGBAIRRO = "idregbairro";
    private String TOTUSER = "totuser";
    private String TOTREG = "totreg";
    private String PAIS = "pais";
    private String ESTADO = "estado";
    private String CIDADE = "cidade";
    private String BAIRRO = "bairro";
    private String UF = "uf";
    private String REGIAO = "regiao";
    private String RESIDUO = "residuo";
    private String DESCRICAO = "descricao";
    private String COR = "cor";
    private String PESO_PONTUACAO = "peso_pontuacao";
    private String PONTUACAO = "pontuacao";

    private String IDENTREGADOR = "identregador";
    private String IDCOLETOR = "idcoletor";
    private String IDUSUARIO = "idusuario";
    private String ENTREGADOR = "entregador";
    private String LOGRADOURO = "logradouro";
    private String DTCADASTRO = "dtcadastro";
    private String DTCOLETA = "dtcoleta";

    private String PESO = "peso";
    private String PESOTOTAL = "pesototal";
    private String PONTOS = "pontos";
    private String CATEG = "categ";

    private String SOMA = "soma";
    private String QTD = "qtd";
    private String COEF = "coef";

    private String IDCOLETA = "idcoleta";
    private String IDENTREGA = "identrega";
    private String DATA = "data";
    private String HRINI = "hr_ini";
    private String HRFIM = "hr_fim";

    private String TIPORESID = "tiporesidencia";
    private String OBS = "observacao";

    private String LATITUDE = "lat";
    private String LONGITUDE = "lon";

    private String ENTAVAL = "ent_aval";
    private String COLAVAL = "col_aval";

    private String LOGIN = "login";
    private String AVATAR = "avatar";

    public static final String DEFAULT_PAIS = "brasil";
    public static final String DEFAULT_ESTADO = "amazonas";
    public static final String DEFAULT_CIDADE = "manaus";
    public static final String DEFAULT_BAIRRO = "centro";

    public static Boolean HasInternet = false;

    private static int DEFAULT_PAIS_ID = -1;
    private static int DEFAULT_ESTADO_ID = -1;
    private static int DEFAULT_CIDADE_ID = -1;
    private static int DEFAULT_BAIRRO_ID = -1;

    //private JSONObject json = null;
    private JSONArray list = null;
    private List<String> mParams = null;

    private Spinner auxSpinner = null;
    private HttpRequestTask reqTask = null;
    private CustomHttpResponse listener = null;

    private Runnable GetPaisRunnable = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.PAIS);
            reqTask.execute();
        }
    };

    private Runnable GetEstadosRunnable = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.ESTADOS);
            reqTask.execute();
        }
    };

    private Runnable GetCidadesRunnable = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.CIDADES);
            reqTask.execute();
        }
    };

    private Runnable GetBairrosRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.BAIRRO);
            reqTask.execute();
        }
    };

    private Runnable GetBairrosRegiaoRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.BAIRROREGIAO);
            reqTask.execute();
        }
    };

    private Runnable GetRegiaoBairrosRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.REGIAOBAIRRO);
            reqTask.execute();
        }
    };

    private Runnable GetRegioesRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.REGIAO);
            reqTask.execute();
        }
    };

    // REMOVER DEPOIS - Utilizar tabela re regiao_bairro ( General ) para filtro
    private Runnable GetRegioesOutRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.REGIAOOUT);
            reqTask.execute();
        }
    };

    private Runnable GetResiduosRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.RESIDUOS);
            reqTask.execute();
        }
    };

    private Runnable GetUserResiduosRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.USERRESIDUOS);
            reqTask.execute();
        }
    };

    private Runnable GetUserRegioesRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.USERREGIOES);
            reqTask.execute();
        }
    };

    private Runnable GetUserRegioesTotRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.USERREGIAOTOT);
            reqTask.execute();
        }
    };

    private Runnable GetUserPontosRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.PONTOS);
            reqTask.execute();
        }
    };

    private Runnable GetUserAvaliacaoRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.AVALIACAO);
            reqTask.execute();
        }
    };

    private Runnable GetTipoResidtRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.TIPORESID);
            reqTask.execute();
        }
    };

    private Runnable GetEntregasNovasRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.ENTREGASNOVAS);
            reqTask.execute();
        }
    };

    private Runnable GetEntregasAceitasRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.ENTREGASACEITAS);
            reqTask.execute();
        }
    };

    private Runnable GetEntregasFinalizadasRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.ENTREGASFINALIZADAS);
            reqTask.execute();
        }
    };

    private Runnable GetEntregaDispsRunnable  = new Runnable() {
        @Override
        public void run() {
             initialize();
             reqTask.setFunction(HttpMessageType.DISPENTREGAS);
            reqTask.execute();
        }
    };

    private Runnable GetEntregaResidsRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.RESIDUOENTREGAS);
            reqTask.execute();
        }
    };

    private Runnable GetAvatarsRunnable  = new Runnable() {
        @Override
        public void run() {
            initialize();
            reqTask.setFunction(HttpMessageType.AVATARS);
            reqTask.execute();
        }
    };

    public void defaultFillArrays(){
        getEstados(null);
        getCidades(null);
        getBairros(null);
        getRegioes(null);
        getResiduos(null);
        getTipoResid(null);
        getRegiaoBairros("");
        getUserAvatars();
    }

    private void initialize(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.LOCATIONS_URL;
        reqTask = new HttpRequestTask(url);
        reqTask.clearParams();
        if (mParams != null){
            reqTask.setmParams(mParams);
        }
        reqTask.setListener(this);
    }

    private void initialize(HttpMessageType function){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.LOCATIONS_URL;
        reqTask = new HttpRequestTask(url);
        reqTask.clearParams();
        if (mParams != null){
            reqTask.setmParams(mParams);
        }
        reqTask.setFunction(function);
        reqTask.setListener(this);
    }

    private void addParam(String param){
        this.mParams = new ArrayList<>();
        mParams.add(param);
    }

    private void clearParams(){
        if (mParams != null){
            mParams.clear();
        }
        if (reqTask != null){
            reqTask.clearParams();
        }
    }

    public Spinner getAuxSpinner() {
        return auxSpinner;
    }

    public void setAuxSpinner(Spinner auxSpinner) {
        this.auxSpinner = auxSpinner;
    }

    public CustomHttpResponse getListener() {
        return listener;
    }

    public void setListener(CustomHttpResponse listener) {
        this.listener = listener;
    }

    public void getPais(Spinner combo){
        try {
            //reqTask.clearParams();
            if (General.getPaises() == null){
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetPaisRunnable);
            }
            else
            {
                fillSpinnerPaises(combo);
            }

        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getEstados(Spinner combo){
        try {
            if (General.getEstados() == null){
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetEstadosRunnable);
            }
            else {
                fillSpinnerEstados(combo);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
     }

    public void getCidades(Spinner combo){
        try {
            if (General.getCidades() == null) {
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetCidadesRunnable);
            }
            else
            {
                fillSpinnerCidades(combo);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getBairros(Spinner combo){
        try {
            if (General.getBairros() == null){
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetBairrosRunnable);
            }
            else
            {
                fillSpinnerBairros(combo);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getRegioes(Spinner combo){
        try {
            if (General.getRegioes() == null){
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetRegioesRunnable);
            }
            else
            {
                fillSpinnerRegioes(combo);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getOutrasRegioes(Spinner combo, List<Integer> regioes){
        try {
            JSONArray allRegs = General.getRegioes();
            if (allRegs == null) {
                clearParams();
                String ids = "";
                String sep = "";
                for (int i = 0; i < regioes.size(); i++) {
                    ids += sep + "regs[]=" + (regioes.get(i));
                    sep ="&";
                }
                addParam(ids);
                setAuxSpinner(combo);
                timerHandler = new Handler();
                timerHandler.post(GetRegioesOutRunnable);
            }
            else
            {
               // synchronized (this) {
                    JSONArray jArray = new JSONArray();
                    for (int i = 0; i < allRegs.length(); i++) {

                        JSONObject reg = (JSONObject) allRegs.get(i);
                        Boolean found = false;
                        for (int j = 0; j < regioes.size(); j++) {
                            if (regioes.get(j) == reg.getInt("id") )  {
                                found = true;
                                break;
                            }
                        }
                        if (!found){
                            jArray.put(reg);   // Preenche a lista com as outras regiões;
                        }
                    }

                    General.setRegioesOut(jArray);
                    fillSpinnerOutrasRegioes(combo);
            //    }
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getResiduos(Spinner combo){
        try {
            if (General.getResiduos() == null){
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetResiduosRunnable);
            }
            else
            {
                fillSpinnerResiduos(combo);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getUserResiduos(int idusuario){
        try {
            if (idusuario > -1) {
                clearParams();
                String strID = "id=" + idusuario;
                addParam(strID);
                timerHandler = new Handler();
                timerHandler.post(GetUserResiduosRunnable);
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getUserRegioes(int idusuario){
        try {
            if (idusuario > -1){
                clearParams();
                String strID = "id=" + idusuario;
                addParam(strID);
                timerHandler = new Handler();
                timerHandler.post(GetUserRegioesRunnable);
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getUserRegioesTot(int idusuario){
        try {
            if (idusuario > -1){
                clearParams();
                String strID = "id=" + idusuario;
                addParam(strID);
                timerHandler = new Handler();
                timerHandler.post(GetUserRegioesTotRunnable);
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getUserPontos(int idusuario){
        try {
            if (idusuario > -1){
                clearParams();
                String strID = "id=" + idusuario;
                addParam(strID);
                timerHandler = new Handler();
                timerHandler.post(GetUserPontosRunnable);
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getUserAvaliacao(int idusuario){
        try {
            if (idusuario > -1){
                clearParams();
                String strID = "id=" + idusuario;
                addParam(strID);
                timerHandler = new Handler();
                timerHandler.post(GetUserAvaliacaoRunnable);
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getUserAvatars(){
        try {

            clearParams();
            timerHandler = new Handler();
            timerHandler.post(GetAvatarsRunnable);

        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getEntregas(int identregador, int idusuario, HttpMessageType tipoEntrega){
        try {
            clearParams();
            StringBuilder sb = new StringBuilder();

            sb.append("identregador=");
            sb.append(identregador);
            sb.append("&idusuario=");
            sb.append(idusuario);
            addParam(sb.toString());
            timerHandler = new Handler();
            switch (tipoEntrega){
                case ENTREGASNOVAS:
                    timerHandler.post(GetEntregasNovasRunnable);
                    break;
                case ENTREGASACEITAS:
                    timerHandler.post(GetEntregasAceitasRunnable);
                    break;
                case ENTREGASFINALIZADAS:
                    timerHandler.post(GetEntregasFinalizadasRunnable);
                    break;
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getEntregaDisps(int identrega){
        try {
            if (identrega > -1){
                clearParams();
                String strID = "identrega=" + identrega;
                addParam(strID);
                timerHandler = new Handler();
                timerHandler.post(GetEntregaDispsRunnable);
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getEntregaResiduos(int identrega){
        try {
            if (identrega > -1){
                clearParams();
                String strID = "identrega=" + identrega;
                addParam(strID);
                timerHandler = new Handler();
                timerHandler.post(GetEntregaResidsRunnable);
            }
        }
        catch( Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getRegiaoBairros(String regiao){
        try {

            JSONArray regBairros = General.getRegiaoBairros();
            if (regBairros == null) {
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetRegiaoBairrosRunnable);
            } else {
                JSONArray filterBairros = null;
                JSONArray bairros = General.getBairros();
                if (bairros != null) {

                    JSONArray regioes = General.getRegioes();
                    JSONObject reg = General.getItemByFieldValue(regioes, REGIAO, regiao);
                    if (reg != null) {

                        String selRegiao = reg.getString("id");
                        filterBairros = new JSONArray();
                        for (int i = 0; i < regBairros.length(); i++) {
                            JSONObject item = ((JSONObject) regBairros.get(i));
                            if (item.getString(IDREGIAO).equalsIgnoreCase(selRegiao)) {

                                JSONObject bairro = new JSONObject();
                                bairro.put(ID, item.getInt(ID));

                                JSONObject bairroAux = General.getItemByFieldValue(bairros, "id", item.getString(IDBAIRRO));
                                bairro.put(BAIRRO, bairroAux.getString(BAIRRO));

                                filterBairros.put( bairro);
                            }
                        }
                        if (filterBairros.length() > 0) {
                            General.setBairrosRegiao(filterBairros);
                            if (listener != null) {
                                JSONObject resp = new JSONObject();
                                resp.put("data", filterBairros);
                                listener.OnHttpResponse(resp, HttpMessageType.REGIAOBAIRRO);
                            }
                        }
                    }

                }
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void getTipoResid(Spinner combo){
        try {
            if (General.getTipoResidencia() == null){
                clearParams();
                timerHandler = new Handler();
                timerHandler.post(GetTipoResidtRunnable);
            }
            else
            {
                fillSpinnerTipoResidencia(combo);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public int getSpinnerItemIndex(Spinner comp, String value) {
        try{
            int cont = comp.getAdapter().getCount();
            for (int i = 0; i < cont; i++){
                Object item = comp.getAdapter().getItem(i);
                //String noAcents = General.removeAcents(value);
                if (item.toString().equalsIgnoreCase(value)){
                    return i;
                }
            }
        }
        catch (Exception ex){

        }

        return -1;
    }

    public void setSpinnerItem(Spinner comp, String value){
        comp.setSelection(getSpinnerItemIndex(comp, value));
        comp.invalidate();
    }

    public void setSpinnerItem(Spinner comp, int idx){
        comp.setSelection(idx);
    }

    public void clearSpinner(Spinner comp){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(General.getAppContext(),
                android.R.layout.simple_spinner_item, new String[]{""});
        comp.setAdapter(adapter);
    }

    private void fillListItems(JSONObject response, HttpMessageType flag){

        try {
            if (response != null) {
                JSONObject json = response;
                if (json.has("data")) {

                    list = json.getJSONArray(Constants.POSTS);
                    if (!json.getString("data").contains("Erro:")) {

                        switch (flag){

                            case BAIRRO:
                                fillSpinnerBairros(null);
                                break;

                            case BAIRROREGIAO:
                                fillListBairrosRegiao();
                                break;

                            case CIDADES:
                                fillSpinnerCidades(null);
                                break;

                            case ESTADOS:
                                fillSpinnerEstados(null);
                                break;

                            case PAIS:
                                fillSpinnerPaises(null);
                                break;

                            case REGIAO:
                                fillSpinnerRegioes(null);
                                break;

                            case REGIAOOUT:
                                fillSpinnerOutrasRegioes(this.getAuxSpinner());
                                break;

                            case REGIAOBAIRRO:
                                fillListRegiaoBairros();
                                break;
                            case RESIDUOS:
                                fillSpinnerResiduos(null);
                                break;
                            case USERRESIDUOS:
                                fillUserResiduos();
                                break;
                            case USERREGIOES:
                                fillUserRegioes();
                                break;
                            case USERREGIAOTOT:
                                fillUserRegioesTot();
                                break;
                            case TIPORESID:
                                fillSpinnerTipoResidencia(null);
                                break;

                            case ENTREGASNOVAS:
                            case ENTREGASACEITAS:
                            case ENTREGASFINALIZADAS:
                                fillUserEntregas(flag);
                                break;

                            case DISPENTREGAS:
                                fillDispEntregas();
                                break;

                            case RESIDUOENTREGAS:
                                fillResiduoEntregas();
                                break;

                            case PONTOS:
                                fillUserPontos();
                                break;

                            case AVALIACAO:
                                fillUserAvaliacao();
                                break;

                            case AVATARS:
                                fillAvatars();
                                General.fillAvatars();
                                break;
                        }
                    }
                }
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private ArrayAdapter<String> generateSpinnerAdapter(String[] items){
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(General.getAppContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }

    private JSONArray fillListPais(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getPaises() == null ){
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);

                    JSONObject newItem = new JSONObject();
                    newItem.put(ID, c.getString(ID));
                    newItem.put(PAIS, c.getString(PAIS));
                    jArray.put(newItem);
                }
                General.setPaises(jArray);
            }
            else{
                jArray = General.getPaises();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListEstados(){

        JSONArray jArray = new JSONArray();
        try{

            if (General.getEstados() == null ){
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);

                    JSONObject newItem = new JSONObject();
                    newItem.put(ID, c.getString(ID));
                    newItem.put(ESTADO, c.getString(ESTADO));
                    newItem.put(UF, c.getString(UF));
                    newItem.put(IDPAIS, c.getString(IDPAIS));
                    jArray.put(newItem);
                }
                General.setEstados(jArray);
            }
            else {
                    jArray = General.getEstados();
                }

            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListCidades(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getCidades() == null ){
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(ID, c.getString(ID));
                    newItem.put(CIDADE, c.getString(CIDADE));
                    newItem.put(IDESTADO, c.getString(IDESTADO));
                    jArray.put(newItem);
                }
                General.setCidades(jArray);
            }
            else {
                jArray = General.getCidades();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListBairros(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getBairros() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(ID, c.getString(ID));
                    newItem.put(BAIRRO, c.getString(BAIRRO));
                    newItem.put(IDCIDADE, c.getString(IDCIDADE));
                    jArray.put(newItem);
                }
                General.setBairros(jArray);
            }
            else {
                jArray = General.getBairros();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListRegioes(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getRegioes() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(ID, c.getString(ID));
                    newItem.put(REGIAO, c.getString(REGIAO));
                    jArray.put(newItem);
                }
                General.setRegioes(jArray);
            }
            else
            {
                jArray = General.getRegioes();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillUserResiduos(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getUserResiduos() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(IDRESIDUO, c.getString(IDRESIDUO));
                    jArray.put(newItem);
                }
                General.setUserResiduos(jArray);
            }
            else
            {
                jArray = General.getUserResiduos();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillUserRegioes(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getUserRegioes() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(IDREGIAO, c.getString(IDREGIAO));
                    newItem.put(IDREGBAIRRO, c.getString(IDREGBAIRRO));
                    jArray.put(newItem);
                }
                General.setUserRegioes(jArray);
            }
            else
            {
                jArray = General.getUserRegioes();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillUserRegioesTot(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getUserRegioesTot() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(IDREGIAO, c.getString(IDREGIAO));
                    newItem.put(TOTUSER, c.getString(TOTUSER));
                    newItem.put(TOTREG, c.getString(TOTREG));
                    jArray.put(newItem);
                }
                General.setUserRegioesTot(jArray);
            }
            else
            {
                jArray = General.getUserRegioesTot();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListBairrosRegiao() {

        JSONArray jArray = new JSONArray();
        try{
            for (int i = 0; i < list.length(); i++) {
                JSONObject c = list.getJSONObject(i);
                JSONObject newItem = new JSONObject();
                // ID TABELA 'REGIAO_BAIRRO'
                newItem.put(ID, c.getString(ID));
                newItem.put(BAIRRO, c.getString(BAIRRO));
                jArray.put(newItem);
            }
            General.setBairrosRegiao(jArray);

            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListRegiaoBairros() {

        JSONArray jArray = new JSONArray();
        try{
            if (General.getRegiaoBairros() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(ID, c.getString(ID));
                    newItem.put(IDBAIRRO, c.getString(IDBAIRRO));
                    newItem.put(IDREGIAO, c.getString(IDREGIAO));
                    jArray.put(newItem);
                }
                General.setRegiaoBairros(jArray);
            }
            else
            {
                jArray = General.getRegiaoBairros();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListRegioesOut() {

        JSONArray jArray = new JSONArray();
        try{
            if (General.getRegioesOut() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();

                    newItem.put("id", c.getString(ID));
                    newItem.put("regiao", c.getString(REGIAO));
                    jArray.put(newItem);
                }
                General.setRegioesOut(jArray);
            }
            else
            {
                jArray = General.getRegioesOut();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListResiduos(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getResiduos() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();

                    newItem.put(ID, c.getString(ID));
                    newItem.put(RESIDUO, c.getString(RESIDUO));
                    newItem.put(DESCRICAO, c.getString(DESCRICAO));
                    newItem.put(COR, c.getString(COR));
                    newItem.put(PESO_PONTUACAO, c.getString(PESO_PONTUACAO));
                    //newItem.put(IMAGEM, c.getString(IMAGEM));
                    jArray.put(newItem);
                }
                General.setResiduos(jArray);
            }
            else
            {
                jArray = General.getResiduos();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillListTipoResidencia(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getTipoResidencia() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();

                    newItem.put(ID, c.getString(ID));
                    newItem.put(DESCRICAO, c.getString(DESCRICAO));
                    jArray.put(newItem);
                }

                General.setTipoResidencia(jArray);
            }
            else
            {
                jArray = General.getTipoResidencia();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray fillUserEntregas(HttpMessageType tipoEntrega){

        JSONArray jArray = new JSONArray();
        try{
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();

                    newItem.put(ID, c.getString(ID));
                    newItem.put(IDENTREGADOR, c.getString(IDENTREGADOR));
                    newItem.put(IDUSUARIO, c.getString(IDUSUARIO));
                    newItem.put(IDCOLETA, c.getString(IDCOLETA));
                    newItem.put(ENTREGADOR, c.getString(ENTREGADOR));
                    newItem.put(LOGRADOURO, c.getString(LOGRADOURO));
                    newItem.put(DTCADASTRO, c.getString(DTCADASTRO));
                    newItem.put(PESOTOTAL, c.getString(PESOTOTAL));
                    newItem.put(PONTOS, c.getString(PONTOS));
                    newItem.put(TIPORESID, c.getString(TIPORESID));
                    newItem.put(BAIRRO, c.getString(BAIRRO));
                    newItem.put(OBS, c.getString(OBS));
                    newItem.put(LATITUDE, c.getString(LATITUDE));
                    newItem.put(LONGITUDE, c.getString(LONGITUDE));
                    newItem.put(ENTAVAL, c.getString(ENTAVAL));
                    newItem.put(COLAVAL, c.getString(COLAVAL));

                    jArray.put(newItem);
                }

                switch (tipoEntrega){
                    case ENTREGASNOVAS:
                        General.setEntregasNovas(jArray);
                        list = null;
                        break;
                    case ENTREGASACEITAS:
                        General.setEntregasAceitas(jArray);
                        list = null;
                        break;
                    case ENTREGASFINALIZADAS:
                        General.setEntregasFinalizadas(jArray);
                        list = null;
                        break;
                }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillDispEntregas(){

        JSONArray jArray = new JSONArray();
        try{
            for (int i = 0; i < list.length(); i++) {
                JSONObject c = list.getJSONObject(i);
                JSONObject newItem = new JSONObject();

                newItem.put(ID, c.getString(ID));
                newItem.put(IDENTREGA, c.getString(IDENTREGA));
                newItem.put(DATA, c.getString(DATA));
                newItem.put(HRINI, c.getString(HRINI));
                newItem.put(HRFIM, c.getString(HRFIM));
                jArray.put(newItem);
            }

            General.setDispEntregas(jArray);

            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillResiduoEntregas(){

        JSONArray jArray = new JSONArray();
        try{
            for (int i = 0; i < list.length(); i++) {
                JSONObject c = list.getJSONObject(i);
                JSONObject newItem = new JSONObject();
                newItem.put(ID, c.getString(ID));
                newItem.put(IDRESIDUO, c.getString(IDRESIDUO));
                newItem.put(PESO, c.getString(PESO));
                newItem.put(PONTOS, c.getString(PONTOS));
                newItem.put(RESIDUO, c.getString(RESIDUO));
                jArray.put(newItem);
            }
            General.setResiduoEntregas(jArray);

            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillUserPontos(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getPontos() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();
                    newItem.put(IDENTREGADOR, c.getString(IDENTREGADOR));
                    newItem.put(PONTOS, c.getString(PONTOS));
                    newItem.put(CATEG, c.getString(CATEG));
                    jArray.put(newItem);
                }
                General.setPontos(jArray);
            }
            else
            {
                jArray = General.getPontos();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillUserAvaliacao(){

        JSONArray jArray = new JSONArray();
        try{
            if (General.getAvaliacao() == null ) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    JSONObject newItem = new JSONObject();

                    newItem.put(IDCOLETOR, c.getString(IDCOLETOR));
                    newItem.put(SOMA, c.getString(SOMA));
                    newItem.put(QTD, c.getString(QTD));
                    newItem.put(COEF, c.getString(COEF));
                    jArray.put(newItem);
                }
                General.setAvaliacao(jArray);
            }
            else
            {
                jArray = General.getAvaliacao();
            }
            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray fillAvatars(){

        JSONArray jArray = new JSONArray();
        try{
            for (int i = 0; i < list.length(); i++) {
                JSONObject c = list.getJSONObject(i);
                JSONObject newItem = new JSONObject();
                newItem.put(ID, c.getString(ID));
                newItem.put(LOGIN, c.getString(LOGIN));
                newItem.put(AVATAR, c.getString(AVATAR));
                jArray.put(newItem);
            }
            General.setAvatars(jArray);

            return jArray;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void fillSpinnerPaises(Spinner combo) {
        fillSpinner(combo, fillListPais(), PAIS, DEFAULT_PAIS);
    }

    public void fillSpinnerEstados(Spinner combo) {
        fillSpinner(combo, fillListEstados(), ESTADO, DEFAULT_ESTADO);
    }

    public void fillSpinnerCidades(Spinner combo) {
        fillSpinner(combo, fillListCidades(), CIDADE, DEFAULT_CIDADE);
    }

    public void fillSpinnerBairros(Spinner combo) {
        fillSpinner(combo, fillListBairros(), BAIRRO, DEFAULT_BAIRRO);
    }

    public void fillSpinnerRegioes(Spinner combo) {
        fillSpinner(combo, fillListRegioes(), REGIAO, null);
    }

    public void fillSpinnerResiduos(Spinner combo) {
        fillSpinner(combo, fillListResiduos(), RESIDUO, null);
    }

    public void fillSpinnerTipoResidencia(Spinner combo) {
        fillSpinner(combo, fillListTipoResidencia(), DESCRICAO, null);
    }

    public void fillSpinnerOutrasRegioes(Spinner combo) {
        fillSpinner(combo, fillListRegioesOut(), REGIAO, null);
    }

    private void setDefaultIndex(String defaultType, int idx){
        if (defaultType.equalsIgnoreCase(DEFAULT_PAIS)){
            DEFAULT_PAIS_ID = idx;
        }
        else if (defaultType.equalsIgnoreCase(DEFAULT_ESTADO)){
            DEFAULT_ESTADO_ID = idx;
        }
        else if (defaultType.equalsIgnoreCase(DEFAULT_CIDADE)){
            DEFAULT_CIDADE_ID = idx;
        }
        else if (defaultType.equalsIgnoreCase(DEFAULT_BAIRRO)){
            DEFAULT_BAIRRO_ID = idx;
        }
    }

    public int getDefaultValue(String defaultType){
        if (defaultType.equalsIgnoreCase(DEFAULT_PAIS)){
            return DEFAULT_PAIS_ID;
        }
        else if (defaultType.equalsIgnoreCase(DEFAULT_ESTADO)){
            return DEFAULT_ESTADO_ID;
        }
        else if (defaultType.equalsIgnoreCase(DEFAULT_CIDADE)){
            return DEFAULT_CIDADE_ID;
        }
        else if (defaultType.equalsIgnoreCase(DEFAULT_BAIRRO)){
            return DEFAULT_BAIRRO_ID;
        }
        return -1;
    }

    private void fillSpinner(Spinner spinner, JSONArray list, String showField, Object defaultValue) {
        try {
            if (spinner != null) {
                String[] items = new String[list.length()];
                int idxDefault = 0;

                for (int i = 0; i < list.length(); i++) {
                    JSONObject c = list.getJSONObject(i);
                    //gets the content of each tag
                    items[i] = c.getString(showField);

                    if (defaultValue != null){
                        if (items[i].equalsIgnoreCase(defaultValue.toString())) {
                            idxDefault = i;
                        }
                    }
                }
                spinner.setAdapter(generateSpinnerAdapter(items));
                if (defaultValue != null) {
                    spinner.setSelection(idxDefault);
                    setDefaultIndex(defaultValue.toString(), idxDefault);
                }
                //Remove o Spinner Auxiliar ...
                this.auxSpinner = null;
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void OnHttpResponse(JSONObject resp, HttpMessageType flag) {
        fillListItems(resp, flag);
        if (listener != null){
            listener.OnHttpResponse(resp, flag);
        }
    }
}
