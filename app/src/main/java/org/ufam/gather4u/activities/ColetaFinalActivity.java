package org.ufam.gather4u.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.fragments.adapter.ResiduoEntregaAdapter;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Entrega;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.Conv;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;
import java.util.List;

public class ColetaFinalActivity extends BaseActivity
        implements View.OnClickListener,
        ClickListener,
        CustomHttpResponse,
        CustomVolleyAdapterInterface
        {

    private final String TAG = this.getClass().getSimpleName();

    private String ID = "id";
    private String IDRESIDUO = "idresiduo";
    private String RESIDUO = "residuo";
    private String PONTOS = "pontos";
    private String PESO = "peso";

    private Entrega mEntrega = null;
    private List<Residuo> mResids = null;

    private TextView tv_entregador = null;
    private TextView tv_pesototal = null;
    private TextView tv_pontos = null;
    private TextView tv_observacao = null;
    private TextView tv_tiporesid = null;
    private TextView tv_bairro = null;
    private TextView tv_logradouro = null;

    private TextView tv_distance = null;
    private TextView tv_duration = null;

    private Button btnFinalizar = null;
    private Button btnFechar = null;

    private RecyclerView mResiduoRecyclerView = null;
    private ResiduoEntregaAdapter mResidAdapter = null;
    private GatherTables mGetTables = null;
    private Gather_User mUser = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION  =   "finalizar";
    public static final String KEY_TOKEN     =   "token";

    public static final String KEY_ENTREGA   =   "identrega";
    public static final String KEY_COLETA    =   "idcoleta";
    public static final String KEY_RESID     =   "residuos";
    public static final String KEY_PONTO     =   "pontos";
    public static final String KEY_PESO      =   "peso";

    protected JsonVolleyAdapter mVlAdapt = null;
    private Double mPontos = 0.0;
    private int askType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta_final);

        mGetTables = new GatherTables();
        mGetTables.setListener(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(getString(R.string.subtitle_tootlbar_confirma_residuos));
        }

        tv_entregador = findViewById(R.id.tv_entregador);
        tv_pesototal = findViewById(R.id.tv_pesototal);
        tv_pontos = findViewById(R.id.tv_pontos);
        tv_entregador = findViewById(R.id.tv_entregador);

        tv_logradouro = findViewById(R.id.tv_logradouro);
        tv_bairro = findViewById(R.id.tv_bairro);
        tv_tiporesid = findViewById(R.id.tv_tiporesid);

        tv_distance = findViewById(R.id.tv_distancia);
        tv_duration = findViewById(R.id.tv_duration);

        mResiduoRecyclerView = findViewById(R.id.residuos_list);

        mResidAdapter = new ResiduoEntregaAdapter(this.getApplication());
        mResidAdapter.setClickListener(this);

        setRecicleView( mResiduoRecyclerView, mResidAdapter );

        mUser = Gather_User.fromJSONObject(General.getLoggedUser());

        if( getIntent().getExtras() != null ){

            try {

                String json = getIntent().getExtras().getString("entrega");

                if (json != null) {
                    mEntrega = Entrega.fromJSONObject(new JSONObject(json));
                    if (mEntrega != null) {
                        mGetTables.getEntregaResiduos(mEntrega.getId());
                    }
                }
            }
            catch (Exception ex){
            }
        }

        btnFinalizar = findViewById( R.id.btn_finalizar);
        btnFinalizar.setOnClickListener(this);

        btnFechar = findViewById( R.id.btn_fechar);
        btnFechar.setOnClickListener(this);

        mVlAdapt = new JsonVolleyAdapter();
    }

    private void setRecicleView( RecyclerView rv, RecyclerView.Adapter adapt){
        if(rv != null) {
            RecyclerView.LayoutManager mLayoutManagerStores = new LinearLayoutManager(this.getApplicationContext());
            rv.setLayoutManager(mLayoutManagerStores);
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.setHasFixedSize(true);
            rv.setNestedScrollingEnabled(true);
            rv.setAdapter(adapt);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mVlAdapt.Start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mVlAdapt.Stop();
    }

    @Override
    public void onResume() {
        if (!isNetworkAvailable()) {
            msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
        }
        super.onResume();
    }

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.COLETAS_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
    }

    private List<Residuo> getFilledResiduos(){
        List<Residuo> result = new ArrayList<Residuo>();
        List<Residuo> listItems = mResidAdapter.getListItem();
        for (int i = 0; i < listItems.size(); i++ ) {
            ResiduoEntregaAdapter.MyViewHolder vh = mResidAdapter.getViewHolder(i);
            String peso = vh.tv_peso.getText().toString();
            if (peso != null){
                if (peso.trim().length() > 0) {
                    if (Conv.ToDouble(peso) > 0){
                        listItems.get(i).setPeso(peso);
                        result.add(listItems.get(i));
                    }
                }
            }

        }
        return result;
    }

    private void finalizarColeta(){

        try {

            setSaveDadosVolleyAdapter();

            if (mResids.size() > 0) {

                Double peso = 0.0;
                Double pesototal = 0.0;
                Double pontuacao = 0.0;
                mPontos = 0.0;

                for (int i = 0; i < mResids.size(); i++) {
                    peso = Double.parseDouble(mResids.get(i).getPeso());
                    pesototal += peso;
                    pontuacao = Double.parseDouble(mResids.get(i).getPesoPontuacao());
                    mPontos += peso * pontuacao;
                }

                JSONObject jUser = new JSONObject();
                String strToken = General.GenerateToken(mUser.getLogin());

                jUser.put(KEY_TOKEN, strToken);
                jUser.put(KEY_ENTREGA, mEntrega.getId());
                jUser.put(KEY_COLETA, mEntrega.getIdColeta());
                jUser.put(KEY_PONTO, mPontos);
                jUser.put(KEY_PESO, pesototal);

                JSONArray resArray = new JSONArray();
                JSONObject jObj = null;
                for (int i = 0; i < mResids.size(); i++) {
                    jObj = new JSONObject();
                    Residuo res = mResids.get(i);
                    jObj.put("id", res.getIdResiduo());
                    jObj.put("residuo", res.getNome());
                    jObj.put("peso", res.getPeso());
                    jObj.put("peso_pontuacao", res.getPesoPontuacao());

                    resArray.put(jObj);
                }
                jUser.put(KEY_RESID, resArray.toString());

                mVlAdapt.setmParams(jUser);
                mVlAdapt.sendRequest();

            }
        }
        catch (Exception ex) {

        }
    }

    private Boolean validateFields() {

        boolean cancel = false;
        mResids = getFilledResiduos();

        if (mResids == null){
            cancel = true;
            msgError("Nenhum resíduo foi informado.");
        }

        if (mResids.size() == 0){
            cancel = true;
            msgError("Nenhum resíduo foi informado.");
        }

        if(cancel) {
            mResiduoRecyclerView.requestFocus();
        }

        return cancel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnHttpResponse(JSONObject response, Constants.HttpMessageType flag) {
        switch (flag){

            case RESIDUOENTREGAS:
                if (response != null) {
                    if (response.has("data")) {
                        ArrayList<Residuo> items = new ArrayList<>();
                        try {
                            JSONArray list = response.getJSONArray(Constants.POSTS);
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject c = list.getJSONObject(i);
                                Residuo newItem = new Residuo();
                                newItem.setId(Integer.valueOf(c.getString(ID)));
                                newItem.setIdResiduo(Integer.valueOf(c.getString(IDRESIDUO)));
                                newItem.setPeso(String.valueOf(c.getDouble(PESO)));
                                newItem.setNome(c.getString(RESIDUO));
                                newItem.setPesoPontuacao(String.valueOf(c.getDouble(PONTOS)));

                                items.add(newItem);
                            }
                            mResidAdapter.setList(items);

                        } catch (Exception ex) {

                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finalizar:
                if (!validateFields()) {
                    askType = 1;
                    msgQuestion("Finalizar a Coleta?");
                }
                break;

            case R.id.btn_fechar:
                onBackPressed();
                break;
        }
    }

    @Override
    public void processDlgReturn(){

        switch (mDlgResult){

            case YES:
                switch (askType){
                    case 1:
                        finalizarColeta();
                        break;
                }
        }
    }

    @Override
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if ( response != null ){
            try {

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("erro")){
                    msgError(jResp.getString("erro"));
                }
                else {
                    if (jResp.has("data")) {

                        if (jResp.get("data").toString().contains("101")) {
                            msgToastOk(this, "Coleta finalizada com sucesso", true);
                        }
                        goToMain();
                    }
                }
            }
            catch (Exception ex){
                msgError("Erro: " + ex.toString());
            }
        }
    }

    @Override
    public void notifyListener(VolleyError error, String flag) {
        Log.i("Script", "Erro: " + error);
        //showToast("Erro: " + error, true);
        msgError(error.toString());
    }

    @Override
    public void onClickListener(View view, int position) {

        if (view != null){

        }
    }

    @Override
    public void onLongClickListener(View view, int position) {

    }
}
