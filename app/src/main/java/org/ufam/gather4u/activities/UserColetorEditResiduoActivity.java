package org.ufam.gather4u.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.activities.fragments.adapter.ResiduoAdapter;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;
import java.util.List;

public class UserColetorEditResiduoActivity extends RequestPermitionsActivity
        implements View.OnClickListener,
        CustomVolleyAdapterInterface,
        CustomHttpResponse {

    private Button mBtnSave = null;
    private GatherTables getLocData = null;
    private Gather_User mGatherUser = null;
    private String mSuccessUpdMsg = "";
    public static final String KEY_RESID        = "residuos";

    private ResiduoAdapter mResiduoAdapter      = null;
    private RecyclerView mRecyclerViewStores    = null;
    private GatherTables getTables = null;
    private List<Integer> mResiduos = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION     = "upd_residuos";
    public static final String KEY_TOKEN        = "token";

    private JsonVolleyAdapter mVlAdapt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coletor_residuo);

        if (actBar != null) {
            actBar = getSupportActionBar();
        }
        actBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerViewStores = (RecyclerView) findViewById(R.id.residuo_list);
        mResiduoAdapter = new ResiduoAdapter(this);
        mResiduoAdapter.setClickListener( new ClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                mResiduoAdapter.clickCheckBox(position);
            }

            @Override
            public void onLongClickListener(View view, int position) {

            } });

        if(mRecyclerViewStores !=null){
            RecyclerView.LayoutManager mLayoutManagerStores = new LinearLayoutManager(this);
            mRecyclerViewStores.setLayoutManager(mLayoutManagerStores);
            mRecyclerViewStores.setItemAnimator(new DefaultItemAnimator());
            mRecyclerViewStores.setHasFixedSize(true);
            mRecyclerViewStores.setNestedScrollingEnabled(true);
            mRecyclerViewStores.setAdapter(mResiduoAdapter);
        }

        try {
            ArrayList<Residuo> items = new ArrayList<>();
            JSONArray residuos = General.getResiduos();
            for(int i=0;i<residuos.length();i++) {
                Residuo r = new Residuo();
                JSONObject regiao = residuos.getJSONObject(i);
                r.setId( regiao.getInt("id"));
                r.setNome( regiao.getString("residuo"));
                r.setDescricao( regiao.getString("descricao"));
                items.add(r);
            }

            Log.e(TAG,"size: "+items.size());
            mResiduoAdapter.setList(items);

        }
        catch (Exception ex){
        }

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

    private void fillUserDataFields(){
        try {
            if ( mGatherUser != null){
                JSONArray residuos = General.getUserResiduos();
                if (residuos != null){
                    int itemIdx = -1;
                    for (int i =0; i < residuos.length(); i++){
                        itemIdx = residuos.getJSONObject(i).getInt("idresiduo") - 1;
                        if (!mResiduoAdapter.getItem(itemIdx).getChecked()){
                            mResiduoAdapter.clickCheckBox( itemIdx );
                        }
                    }
                }
                else {
                    getTables = new GatherTables();
                    getTables.setListener(this);
                    getTables.getUserResiduos(mGatherUser.getId());
                }
            }
        }
        catch (Exception ex){
        }
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

                mResiduos = getIdsCheckResiduos();

                 JSONArray arr = new JSONArray();
                 for (int id : mResiduos) {
                     JSONObject ids = new JSONObject();
                     ids.put("idresiduo", id);
                     arr.put(ids);
                 }
                 jUser.put(KEY_RESID, arr.toString());


                setSaveDadosVolleyAdapter();
                mVlAdapt.setmParams(jUser);
                mVlAdapt.sendRequest();
            }

        } catch (Exception e) {
        }

    }

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.UPD_USUARIOS_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
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
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if (response != null){

            dismissLoadingDialog();

            switch (flag){
                case "USERRESIDUOS": {
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

            try {
                if (response.has("data")){
                    if (response.getString("data").contains("100")){

                        // Envia email de boas vindas
                        if (mGatherUser != null){
                            General.setLoggedUser( new JSONObject(mGatherUser.toString()) );
//                            EmailSender sender = new EmailSender();
//                            sender.sendMessage( mGatherUser.getEmail(),
//                                    "Atualização de cadastro - Gather4u",
//                                    mSuccessUpdMsg);
                        }
                        msgToastOk(this, "Atualização realizada com sucesso", true);
                    }
                }
                else if (response.has("erro")){
                    if (response.getString("data").contains("666")){
                        msgError("Erro ao atualizar Resíduos");
                    }
                }
            }
            catch (Exception ex){
                msgError(ex.getMessage());
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

    private List<Integer> getIdsCheckResiduos(){

        List<Integer> result = new ArrayList<Integer>();
        List<Residuo> listItems = mResiduoAdapter.getListItem();
        for (int i = 0; i < listItems.size(); i++ ) {
            if (listItems.get(i).getChecked() == Boolean.TRUE) {
                result.add(listItems.get(i).getId());
            }
        }
        return result;
    }

    private Boolean validateFields(){

        boolean cancel = false;
        List<Integer> chks = getIdsCheckResiduos();
        cancel = chks.size() == 0;
        if(cancel) {
            msgError("Nenhum resíduo foi selecionado.");
            mRecyclerViewStores.requestFocus();
        }
        return cancel;
    }
}
