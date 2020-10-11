package org.ufam.gather4u.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Entrega;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.GatherTables;

public class ColetaAvaliacaoActivity extends BaseActivity
        implements View.OnClickListener, CustomVolleyAdapterInterface,
        RatingBar.OnRatingBarChangeListener{

    private final String TAG = this.getClass().getSimpleName();

    private Entrega mEntrega = null;
    private Gather_User mGatherUser = null;

    private TextView tv_title = null;
    private TextView tv_description = null;
    private RatingBar rb_avaliacao = null;
    private EditText input_opiniao = null;

    private Button btnConfirmar = null;
    private Button btnFechar = null;

    private GatherTables mGetTables = null;
    private Gather_User mUser = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION         =   "avaliar";

    public static final String KEY_TOKEN            =   "token";
    public static final String KEY_COLETA           =   "idcoleta";
    public static final String KEY_ENTREGA          =   "identrega";
    public static final String KEY_TIPO             =   "tipo";
    public static final String KEY_RATING           =   "nota";
    public static final String KEY_DESCRICAO        =   "descricao";

    protected JsonVolleyAdapter mVlAdapt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta_avaliacao);

        //mGetTables = new GatherTables();
        //mGetTables.setListener(this);

        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_rating_description);

        rb_avaliacao = findViewById(R.id.rb_avaliacao);
        rb_avaliacao.setOnRatingBarChangeListener( this );

        input_opiniao = findViewById(R.id.input_opiniao);

        mUser = Gather_User.fromJSONObject(General.getLoggedUser());

        if( getIntent().getExtras() != null ){

            try {
                String json = getIntent().getExtras().getString("entrega");
                if (json != null){
                    mEntrega = Entrega.fromJSONObject(new JSONObject(json));
                }
            }
            catch (Exception ex){
            }
        }

        btnConfirmar = findViewById( R.id.btn_confirmar);
        btnConfirmar.setOnClickListener(this);

        btnFechar = findViewById( R.id.btn_fechar);
        btnFechar.setOnClickListener(this);

        switch (General.getUserType()){
            case 2:
                tv_title.setText(getString(R.string.subtitle_tootlbar_avaliar_coleta));

                if(getSupportActionBar()!=null){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setSubtitle(getString(R.string.subtitle_tootlbar_avaliar_coleta));
                }
                break;

            case 3:
                tv_title.setText( getString(R.string.subtitle_tootlbar_avaliar_entrega) );

                if(getSupportActionBar()!=null){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setSubtitle(getString(R.string.subtitle_tootlbar_avaliar_entrega));
                }
                break;
        }

        mVlAdapt = new JsonVolleyAdapter();

        input_opiniao.requestFocus();
        hideKeyboard();
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

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.COLETAS_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
    }

    private void avaliarColeta(){

        setSaveDadosVolleyAdapter();
        try {

            mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );
            if (mGatherUser != null){

                JSONObject jUser = new JSONObject();
                String strToken = General.GenerateToken(mGatherUser.getLogin());
                jUser.put(KEY_TOKEN, strToken);
                jUser.put(KEY_ENTREGA, -1);
                jUser.put(KEY_COLETA, -1);

                switch (General.getUserType()) {
                    case 2:
                        jUser.put(KEY_TIPO, 2);
                        jUser.put(KEY_COLETA, mEntrega.getIdColeta());
                        break;

                    case 3:
                        jUser.put(KEY_TIPO, 1);
                        jUser.put(KEY_ENTREGA, mEntrega.getId());
                        break;
                }

                float rate = rb_avaliacao.getRating();
                String opiniao = input_opiniao.getText().toString();

                jUser.put(KEY_RATING, rate);
                jUser.put(KEY_DESCRICAO, opiniao);

                mVlAdapt.setmParams(jUser);
                mVlAdapt.sendRequest();
            }

        } catch (Exception e) {
        }
    }

    private Boolean validateFields() {

        boolean cancel = false;
        View focusView = null;

        String opiniao = input_opiniao.getText().toString().trim();

        if( rb_avaliacao.getRating() <= 0){
            cancel = true;
            focusView = rb_avaliacao;
            msgError("Clique nas estrelinhas para avaliar");
        }

        if(cancel) {
            focusView.requestFocus();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirmar:
                if (!validateFields()) {
                    msgQuestion("Confirma esta avaliação?");
                }
                break;

            case R.id.btn_fechar:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onResume() {
        if (!isNetworkAvailable()) {
            msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
        }
        super.onResume();
    }

    @Override
    public void processDlgReturn(){

        switch (mDlgResult){
            case YES:
                avaliarColeta();
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
                        msgToastOk(this, "Avaliação realizada com sucesso", true);
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
    public void notifyListener(VolleyError erro, String flag) {
        Log.i("Script", "Erro: " + erro);
        dismissLoadingDialog();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        if (ratingBar != null){
            if (fromUser){
                String msgOpiniao = "";
                if ( rating > 0 ){

                    if (rating <= 1){
                        msgOpiniao = getString(R.string.hint_rating_odiei);
                    }
                    else if (rating <= 2){
                        msgOpiniao = getString(R.string.hint_rating_nao_gostei);
                    }
                    else if (rating <= 3){
                        msgOpiniao = getString(R.string.hint_rating_satisfatoria);
                    }
                    else if (rating <= 4){
                        msgOpiniao = getString(R.string.hint_rating_gostei);
                    }
                    else if (rating > 4){
                        msgOpiniao = getString(R.string.hint_rating_adorei);
                    }
                }
                tv_description.setText( msgOpiniao );
            }
        }
    }
}
