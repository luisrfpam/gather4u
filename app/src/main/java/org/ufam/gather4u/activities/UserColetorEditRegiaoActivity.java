package org.ufam.gather4u.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.activities.fragments.adapter.BairroAdapter;
import org.ufam.gather4u.activities.fragments.adapter.RegiaoAdapter;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Bairro;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Regiao;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;
import java.util.List;

public class UserColetorEditRegiaoActivity extends RequestPermitionsActivity
        implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        CustomVolleyAdapterInterface,
        CustomHttpResponse {

    private Button mBtnSave = null;
    private Spinner mInputRegiao = null;
    private Gather_User mGatherUser = null;
    private RegiaoAdapter mRegiaoAdapter = null;
    private BairroAdapter mBairroAdapter = null;
    private RecyclerView mRecyclerViewStoresRegiao = null;
    private RecyclerView mRecyclerViewStoresBairro = null;
    private List<Integer> mRegioes = null;
    private List<Integer> mBairros = null;
    private String mSuccessUpdMsg = "";
    private String mFirstSelectedRegiao = "";
    private int mBairrosIdx = -1;

    // PHP Function from the http Request;
    public static final String KEY_REGIOES  =   "regioes";
    public static final String KEY_BAIRROS  =   "bairros";
    private GatherTables getLocData = null;
    public static final String KEY_FUNCTION     = "upd_regioes";
    public static final String KEY_TOKEN        = "token";

    private JsonVolleyAdapter mVlAdapt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coletor_regiao);

        getLocData = new GatherTables();

        if (actBar != null) {
            actBar = getSupportActionBar();
        }
        actBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerViewStoresRegiao = (RecyclerView) findViewById(R.id.regiao_list);

        mRecyclerViewStoresBairro = (RecyclerView) findViewById(R.id.bairros_list);

        mInputRegiao = (Spinner) findViewById(R.id.input_regioes);
        mInputRegiao.setOnItemSelectedListener(this);

        getLocData.getRegioes(mInputRegiao);

        //LayoutInflater layoutInflater = (LayoutInflater) mActParent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();

        // ------------------------------------------------------------------------------------
        mRegiaoAdapter = new RegiaoAdapter(this, layoutInflater);
        RecyclerView.LayoutManager layoutManagerStoresRegiao = new LinearLayoutManager(this);

        mRegiaoAdapter.setClickListener(new ClickListener() {
            @Override
            public void onClickListener(View view, int position) {

                if (view != null){
                    synchronized(this) {
                        getLocData.clearSpinner(mInputRegiao);
                        mRegiaoAdapter.clickCheckBox(position);
                        fillOutrasRegioes();
                    }
                }
            }

            @Override
            public void onLongClickListener(View view, int position) {

            } });

        mRegiaoAdapter.setChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fillOutrasRegioes();
            }
        });

        if(mRecyclerViewStoresRegiao !=null){
            mRecyclerViewStoresRegiao.setLayoutManager(layoutManagerStoresRegiao);
            mRecyclerViewStoresRegiao.setItemAnimator(new DefaultItemAnimator());
            mRecyclerViewStoresRegiao.setHasFixedSize(true);
            mRecyclerViewStoresRegiao.setNestedScrollingEnabled(false);
            mRecyclerViewStoresRegiao.setAdapter(mRegiaoAdapter);
        }

        try {
            ArrayList<Regiao> items = new ArrayList<>();
            JSONArray regioes = General.getRegioes();
            for(int i=0;i<regioes.length();i++) {
                Regiao r = new Regiao();
                JSONObject regiao = regioes.getJSONObject(i);
                r.setId( regiao.getInt("id"));
                r.setNome( regiao.getString("regiao"));
                items.add(r);
            }

            Log.e(TAG,"size: " + items.size());
            mRegiaoAdapter.setList(items);
        }
        catch (Exception ex){
        }

        // ------------------------------------------------------------------------------------
        newBairroAdapter();

        mBtnSave = (Button) findViewById(R.id.btn_update);
        mBtnSave.setOnClickListener(this);

        new Handler().post(new Runnable() {
            public void run() {
                mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );
                fillUserDataFields();
            }
        });

        mVlAdapt = new JsonVolleyAdapter();

        hideKeyboard();
    }

    private void fillUserBairros(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    //mRecyclerViewStoresBairro.smoothScrollToPosition(mBairroAdapter.getItemCount());
                    // Preencher o spinner de bairros e a lista de bairros
                    JSONArray regBairros = General.getUserRegioes();
                    JSONObject reg = null;
                    for (int i = 0; i < regBairros.length(); i++) {
                        reg = regBairros.getJSONObject(i);
                        if (reg != null) {
                            //idregiao, idregbairro
                            int itemIdx = reg.getInt("idregiao") - 1;
                            if (itemIdx == mBairrosIdx) {
                                int idRegBairro = reg.getInt("idregbairro");

                                for (int x = 0; x < mBairroAdapter.getViewsCount(); x++){

                                    Bairro bairro = mBairroAdapter.getItem(x);
                                    if (bairro.getId() == idRegBairro){
                                        if (!bairro.getChecked()) {
                                            mBairroAdapter.clickCheckBox(x);
                                        }
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
                catch (Exception ex){
                    String msgErro = ex.getMessage();
                }

            }
        }, 1000);
    }

    private void fillUserDataFields(){
        try {
            if ( mGatherUser != null){

                JSONArray regTot = General.getUserRegioesTot();
                if (regTot != null) {
                    int itemIdx = -1;

                    int bairrosIdx = -1;
                    JSONObject reg = null;
                    for (int i = 0; i < regTot.length(); i++) {
                        reg = regTot.getJSONObject(i);
                        if (reg != null) {
                            itemIdx = reg.getInt("idregiao") - 1;
                            if (reg.getInt("totuser") == reg.getInt("totreg")) {
                                if (!mRegiaoAdapter.getItem(itemIdx).getChecked()){
                                    mRegiaoAdapter.clickCheckBox(itemIdx);
                                }
                            } else {
                                mBairrosIdx = itemIdx;
                            }
                        }
                    }

                    fillOutrasRegioes();
                    fillUserBairros();
                }
            }
        }
        catch (Exception ex){
            String msgErro = ex.getMessage();
        }
    }

    private List<Integer> getIdsCheckRegioes(){

        List<Integer> result = new ArrayList<Integer>();
        List<Regiao> listItems = mRegiaoAdapter.getListItem();
        for (int i = 0; i < listItems.size(); i++) {
            Regiao reg = listItems.get(i);
            if (reg.getChecked() == Boolean.TRUE) {
                result.add(reg.getId());
            }
        }

        return result;
    }

    private List<Integer> getIdsCheckBairros(){

        List<Integer> result = new ArrayList<Integer>();
        List<Bairro> listItems = mBairroAdapter.getListItem();
        for (int i = 0; i < listItems.size(); i++ ) {
            Bairro bairro = listItems.get(i);
            if (bairro.getChecked() == Boolean.TRUE) {
                result.add(bairro.getId());
            }
        }
        return result;
    }

    private Boolean validateFields(){
        boolean cancel = false;

        mRegioes = getIdsCheckRegioes();
        mBairros = getIdsCheckBairros();

        if(mRegioes.size() == 0) {
            // Se não marcou região ... verifica bairros
            cancel = mRegioes.size() == 0 && mBairros.size() == 0;
            if(cancel) {
                msgError("Selecione uma região ou bairro para prosseguir.");
                mRecyclerViewStoresRegiao.requestFocus();
            }
        }
        return cancel;
    }


    private void UpdateUser() {

        try {
             if (mGatherUser != null) {

                 showLoadingDialog();

                 mSuccessUpdMsg = "\n\r" +
                        mGatherUser.getNome() + "\n\r" +
                        "Cadastro atualizado com sucesso. \n\r" +
                        "\n\r" +
                        "Dados de login: \n\r" +
                        "Login: " + mGatherUser.getLogin() + "\n\r" +
                        "Senha: " + mGatherUser.getSenha() + "\n\r" +
                        "Acesse agora mesmo.";

                JSONObject jUser = new JSONObject();
                String strToken = General.GenerateToken(mGatherUser.getLogin());
                jUser.put(KEY_TOKEN, strToken);
                jUser.put("id", mGatherUser.getId());

                 // Retorna os ID de cada região( Não índice !!!)
                 mRegioes = getIdsCheckRegioes();

                 // Adiciona os bairros selecionados a listagem ge regiões
                 mBairros = getIdsCheckBairros();

                 //mGatherUser, mResiduos

                 JSONArray arr = new JSONArray();
                 JSONObject ids = null;
                 for (int id : mRegioes) {
                     ids = new JSONObject();
                     ids.put("idregiao", id);
                     arr.put(ids);
                 }
                 jUser.put(KEY_REGIOES, arr.toString());

                 arr = new JSONArray();
                 for (int id : mBairros) {
                     ids = new JSONObject();
                     ids.put("idoutrobairro", id);
                     arr.put(ids);
                 }
                 jUser.put(KEY_BAIRROS, arr.toString());

                 setSaveDadosVolleyAdapter();
                 mVlAdapt.setmParams(jUser);
                 mVlAdapt.sendRequest();
            }

        } catch (Exception e) {
        }

    }

    private void newBairroAdapter(){

        mBairroAdapter = new BairroAdapter(getLayoutInflater());
        getLocData.setListener(mBairroAdapter);
        RecyclerView.LayoutManager layoutManagerStoresBairro = new LinearLayoutManager(this);
        mBairroAdapter.setAux(mRecyclerViewStoresBairro);
        mBairroAdapter.setClickListener(new ClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                mBairroAdapter.clickCheckBox(position);
            }

            @Override
            public void onLongClickListener(View view, int position) {

            } });

        if(mRecyclerViewStoresBairro !=null) {
            mRecyclerViewStoresBairro.setLayoutManager(layoutManagerStoresBairro);
            mRecyclerViewStoresBairro.setItemAnimator(new DefaultItemAnimator());
            mRecyclerViewStoresBairro.setHasFixedSize(true);
            mRecyclerViewStoresBairro.setNestedScrollingEnabled(false);
            mRecyclerViewStoresBairro.setAdapter(mBairroAdapter);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerViewStoresBairro.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (v != null){
                        fillUserBairros();
                    }
                }
            });
        }
    }

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.UPD_USUARIOS_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
    }

    private void fillOutrasRegioes()
    {
        int len = mRegiaoAdapter.getItemCount();
        List<Integer> chkLst = getIdsCheckRegioes();
        if (chkLst.size() < len){
            getLocData.getOutrasRegioes(mInputRegiao, chkLst);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillUserDataFields();
    }

    @Override
    public void onStart() {
        super.onStart();
        mVlAdapt.Start();
    }

    @Override
    public void onStop() {
        mVlAdapt.Stop();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
//                if (mPager.getCurrentItem() > 0) {
//                    // movePreviousFragment();
//                    return true;
//                } else
                    onBackPressed();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.btn_update:
                if (!validateFields()){
                    UpdateUser();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (view != null){
            String regiao = ((TextView) view).getText().toString();

            if (regiao.trim().length() > 0) {
                if (mFirstSelectedRegiao.trim().length() == 0 || !mFirstSelectedRegiao.equalsIgnoreCase(regiao)) {
                    mFirstSelectedRegiao = regiao;
//                newBairroAdapter();
                    getLocData.getRegiaoBairros(regiao);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if (response != null) {
            try {
                dismissLoadingDialog();

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("data")) {
                    if (jResp.get("data").toString().contains("100")) {
                        if (jResp.get("data").toString().contains("100")){

                            // Envia email de boas vindas
                            if (mGatherUser != null){
//                                EmailSender sender = new EmailSender();
//                                sender.sendMessage( mGatherUser.getEmail(),
//                                        "Atualização de cadastro - Gather4u",
//                                        mSuccessUpdMsg);
                            }
                            msgToastOk(this, "Atualização realizada com sucesso", true);
                        }
                    }
                }
                else if (jResp.has("erro")) {
                    msgToastError( jResp.get("erro").toString() );
                }
            } catch (Exception ex) {
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
    public void OnHttpResponse(JSONObject response, Constants.HttpMessageType flag) {
        if (response != null){
            switch (flag){
                case USERRESIDUOS: {
                    JSONArray jArray = new JSONArray();
                    try {
                        String IDRESIDUO = "idresiduo";
                        JSONArray list = response.getJSONArray(Constants.POSTS);
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject c = list.getJSONObject(i);
                            JSONObject newItem = new JSONObject();
                            newItem.put(IDRESIDUO, c.getString(IDRESIDUO));
                            jArray.put(newItem);
                        }
                        General.setUserResiduos(jArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fillUserDataFields();
                }
                break;
            }
        }
    }

}
