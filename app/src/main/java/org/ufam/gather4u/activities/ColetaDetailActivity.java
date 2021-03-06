package org.ufam.gather4u.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.fragments.adapter.DisponibilidadeDetalheAdapter;
import org.ufam.gather4u.activities.fragments.adapter.ResiduoDetalheAdapter;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.interfaces.CustomMapInterface;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Disponibilidade;
import org.ufam.gather4u.models.Entrega;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.CustomGson;
import org.ufam.gather4u.utils.GPSLocation;
import org.ufam.gather4u.utils.GatherMapFragment;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;

public class ColetaDetailActivity extends BaseActivity
        implements View.OnClickListener,
        CustomHttpResponse,
        CustomVolleyAdapterInterface,
        CustomMapInterface {

    private final String TAG = this.getClass().getSimpleName();

    private String ID = "id";
    private String DATA = "data";
    private String HRINI = "hr_ini";
    private String HRFIM = "hr_fim";
    private String RESIDUO = "residuo";
    private String PONTOS = "pontos";
    private String PESO = "peso";

    private LocationListener listener = null;
    private ScrollView mScrollView;
    private GatherMapFragment mMap = null;
    private Entrega mEntrega = null;

    private TextView tv_entregador = null;
    private TextView tv_pesototal = null;
    private TextView tv_pontos = null;
    private TextView tv_observacao = null;
    private TextView tv_tiporesid = null;
    private TextView tv_bairro = null;
    private TextView tv_logradouro = null;
    private TextView tv_lblPontos = null;

    private TextView tv_distance = null;
    private TextView tv_duration = null;

    private Button btnFinalizar = null;
    private Button btnFechar = null;
    private Button btnCancelar = null;
    private Button btnAvaliar = null;

    private RecyclerView mDispRecyclerView = null;
    private RecyclerView mResiduoRecyclerView = null;
    private ResiduoDetalheAdapter mResidAdapter = null;
    private DisponibilidadeDetalheAdapter mDispAdapter = null;
    private GatherTables mGetTables = null;
    private Gather_User mUser = null;
    private GPSLocation location = null;

    public Constants.FragmentType mType;

    // PHP Function from the http Request;

    public static final String KEY_CANCEL           =   "cancelar";
    public static final String KEY_TOKEN            =   "token";

    public static final String KEY_ENTREGA          =   "identrega";
    public static final String KEY_COLETA           =   "idcoleta";
    public static final String KEY_MOTIVO           =   "motivo";

    private int askType = -1;

    protected JsonVolleyAdapter mVlAdapt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta_detail);

        mGetTables = new GatherTables();
        mGetTables.setListener(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(getString(R.string.subtitle_tootlbar_coleta_details));
        }

        tv_entregador = findViewById(R.id.tv_entregador);
        tv_pesototal = findViewById(R.id.tv_pesototal);
        tv_pontos = findViewById(R.id.tv_pontos);
        tv_lblPontos = findViewById(R.id.lblPontos);
        tv_entregador = findViewById(R.id.tv_entregador);
        tv_observacao = findViewById(R.id.tv_observacao);
        tv_logradouro = findViewById(R.id.tv_logradouro);
        tv_tiporesid = findViewById(R.id.tv_tiporesid);
        tv_bairro = findViewById(R.id.tv_bairro);
        tv_distance = findViewById(R.id.tv_distancia);
        tv_duration = findViewById(R.id.tv_duration);

        mDispRecyclerView = findViewById(R.id.disponibilidade_list);
        mResiduoRecyclerView = findViewById(R.id.residuos_list);
        mScrollView = (ScrollView) findViewById(R.id.sv_container);

        mMap = (GatherMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.setListener(new GatherMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        mDispAdapter = new DisponibilidadeDetalheAdapter(this.getApplication());
        //mDispAdapter.setClickListener(this);

        mResidAdapter = new ResiduoDetalheAdapter(this.getApplication());
        //mResidAdapter.setClickListener(this);

        setRecicleView( mDispRecyclerView, mDispAdapter );
        setRecicleView( mResiduoRecyclerView, mResidAdapter );

        mUser = Gather_User.fromJSONObject(General.getLoggedUser());

        if( getIntent().getExtras() != null ){

            try {
                String json = getIntent().getExtras().getString("entrega");
                if (json != null){
                    mEntrega = Entrega.fromJSONObject(new JSONObject(json));

                    if (mEntrega != null){
                        tv_entregador.setText( mEntrega.getEntregador() );
                        tv_pesototal.setText( mEntrega.getPesoTotal() );
                        tv_pontos.setText( mEntrega.getPontos() );
                        tv_tiporesid.setText( mEntrega.getTipoResidencia() );
                        tv_logradouro.setText( mEntrega.getLogradouro() );
                        tv_bairro.setText( mEntrega.getBairro() );
                        tv_observacao.setText( mEntrega.getObs() );

                        mGetTables.getEntregaDisps( mEntrega.getId() );
                        mGetTables.getEntregaResiduos( mEntrega.getId() );
                    }
                }

                Object obj = getIntent().getExtras().get("tipo");
                if (obj != null){
                    mType = (Constants.FragmentType)obj;
                }
            }
            catch (Exception ex){
            }
        }

        if (mMap != null){
            location = new GPSLocation(this, this);
            location.setFragment(mMap);
            location.setLocListener(this);
            location.Start();
            location.setCanClick(false);
        }

        btnFinalizar = findViewById( R.id.btn_finalizar );
        btnFinalizar.setOnClickListener(this);

        btnFechar = findViewById( R.id.btn_fechar);
        btnFechar.setOnClickListener(this);

        btnCancelar = findViewById( R.id.btn_cancelar);
        btnCancelar.setOnClickListener(this);

        btnAvaliar = findViewById( R.id.btn_avaliar);
        btnAvaliar.setOnClickListener(this);
        btnAvaliar.setVisibility(View.GONE);

        switch (General.getUserType()){
            case 2:
                btnFinalizar.setVisibility(View.GONE );
                if (mType == Constants.FragmentType.MainSwapRealizadas)
                {
                    btnCancelar.setVisibility(View.GONE);
                }

                switch (mType)
                {
                    case MainSwapRealizadas:

                        if (mEntrega.getColAval() == 0){
                            btnAvaliar.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                break;

            case 3:

                tv_pontos.setVisibility(View.GONE);
                tv_lblPontos.setVisibility(View.GONE);

                switch (mType)
                {
                    case MainSwapRealizadas:

                        btnFinalizar.setVisibility(View.GONE );
                        btnCancelar.setVisibility(View.GONE);

                        if (mEntrega.getEntAval() == 0){
                            btnAvaliar.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                break;
        }

        mVlAdapt = new JsonVolleyAdapter();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mEntrega != null){

                    double _lat = mEntrega.getLatitude();
                    double _lon = mEntrega.getLongitude();
                    if (_lat != 0 && _lon != 0){
                        location.AddPoint( new LatLng(_lat, _lon) );
                        location.checkDrawRoute();
                    }
                }
            }
        }, 1000);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                if (mEntrega != null){

                    double _lat = mEntrega.getLatitude();
                    double _lon = mEntrega.getLongitude();
                    if (_lat != 0 && _lon != 0){
                        location.AddPoint( new LatLng(_lat, _lon) );
                        location.checkDrawRoute();
                    }
                }
            }
        };
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

    private void setCancelDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.CANC_COLETAS_URL;
        mVlAdapt.setFunction(KEY_CANCEL);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
    }

    private void cancelarEntrega(){
        try {

            if (mMsg != null){

                showLoadingDialog();
                setCancelDadosVolleyAdapter();

                String msg = mMsg.getText().toString();
                if (msg.trim().length() > 0 ){

                    JSONObject jUser = new JSONObject();
                    String strToken = General.GenerateToken(mUser.getLogin());
                    jUser.put(KEY_TOKEN, strToken);
                    jUser.put(KEY_ENTREGA, mEntrega.getId() );
                    jUser.put(KEY_COLETA, mEntrega.getIdColeta() );
                    jUser.put(KEY_MOTIVO, msg );
                    mVlAdapt.setmParams(jUser);
                    mVlAdapt.sendRequest();
                }
                else
                {
                    mDlgResult = Constants.MsgResult.OK;
                    //msgError("Preencha o motivo do cancelamento");
                    processDlgReturn();
                }
            }
            else
            {
                showDlgMotivo(this, "Preencha o motivo do cancelamento");
            }
        }
        catch (Exception ex) {
        }
    }

    private void finalizarColeta(){

        Intent intent = new Intent(this, ColetaFinalActivity.class);
        String strItem = CustomGson.ToJson( mEntrega );
        intent.putExtra("entrega", strItem);
        startActivity(intent);
    }

    private void avaliarColeta(){

        Intent intent = new Intent(this, ColetaAvaliacaoActivity.class);
        String strItem = CustomGson.ToJson( mEntrega );
        intent.putExtra("entrega", strItem);
        startActivity(intent);
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
            case DISPENTREGAS:
                if (response != null) {

                    if (response.has("data")) {
                        ArrayList<Disponibilidade> items = new ArrayList<>();
                        try {
                            JSONArray list = response.getJSONArray(Constants.POSTS);
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject c = list.getJSONObject(i);
                                Disponibilidade newItem = new Disponibilidade();

                                newItem.setData(c.getString(DATA));
                                newItem.setHrini(c.getString(HRINI));
                                newItem.setHrfim(c.getString(HRFIM));

                                items.add(newItem);
                            }
                            mDispAdapter.setList(items);

                        } catch (Exception ex) {

                        }
                    }
                }
                break;

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
                askType = 1;
                msgQuestion("Deseja finalizar a Coleta?");
                break;

            case R.id.btn_cancelar:
                askType = 2;
                msgQuestion("Deseja cancelar a Coleta?");
                break;

            case R.id.btn_fechar:
                onBackPressed();
                break;

            case R.id.btn_avaliar:
                askType = 3;
                switch (General.getUserType()) {
                    case 2:
                        msgQuestion("Deseja avaliar a Coleta?");
                        break;
                    case 3:
                        msgQuestion("Deseja avaliar a Entrega?");
                        break;
                }
        }
    }

    @Override
    public void processDlgReturn(){

        switch (mDlgResult){
            case OK:
                switch (askType) {
                    case 2:
                        if (mMsg != null){
                            String msg = mMsg.getText().toString();
                            if (msg.trim().length() > 0 ) {
                                cancelarEntrega();
                            }
                            else {
                                showDlgMotivo(this, "Preencha o motivo do cancelamento");
                            }
                        }
                        break;
                }
                break;

            case YES:
                switch (askType){
                    case 1:
                        finalizarColeta();
                        break;
                    case 2:
                        cancelarEntrega();
                        break;
                    case 3:
                        avaliarColeta();
                        break;
                }
                break;
        }
    }

    @Override
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if ( response != null ){
            try {

                dismissLoadingDialog();

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("erro")){
                    msgError(jResp.getString("erro"));
                }
                else
                    if (jResp.has("data")){

                    if (jResp.get("data").toString().contains("100")){
                        msgToastOk(this, "Coleta agendada com sucesso", true);
                    }
                    else
                    if (jResp.get("data").toString().contains("101")){
                        msgToastOk(this, "Coleta cancelada com sucesso", true);
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
        dismissLoadingDialog();
        //showToast("Erro: " + error, true);
        msgError(error.toString());
    }

    @Override
    public String getDistance() {  return null; }

    @Override
    public void setDistance( String mDistance ) {
        if ( mDistance != null ){
            tv_distance.setText( mDistance );
        }
    }

    @Override
    public String getDuration() {  return null;  }

    @Override
    public void setDuration( String mDuration ) {
        if ( mDuration != null ){
            tv_duration.setText( mDuration );
        }
    }
}
