package org.ufam.gather4u.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.services.JsonVolleyAdapter;

public class UserUpdatePasswordActivity extends RequestPermitionsActivity implements
        View.OnClickListener,
        CustomVolleyAdapterInterface { //implements OnMapReadyCallback {

    private final String TAG = this.getClass().getSimpleName();
    private Gather_User mGatherUser = null;
    private EditText mInputSenha = null;
    private EditText mInputConfirma = null;
    private Button mBtnSave = null;
    private JsonVolleyAdapter mVlAdapt = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION =   "updpassword";
    public static final String KEY_TOKEN    =   "token";
    public static final String KEY_USER     =   "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_password);

        mInputSenha = (EditText) findViewById(R.id.input_senha);
        mInputConfirma = (EditText) findViewById(R.id.input_confirma);
        mGatherUser = Gather_User.fromJSONObject(General.getLoggedUser());

        if (actBar != null) {
            actBar = getSupportActionBar();
        }
        actBar.setDisplayHomeAsUpEnabled(true);

        //mapFragment.getMapAsync(this);
        mVlAdapt = new JsonVolleyAdapter();
        setSaveDadosVolleyAdapter();

        mBtnSave = (Button) findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);

        hideKeyboard();
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
    public void onResume() {
        if (!isNetworkAvailable()) {
            msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
        }
        super.onResume();
    }

    @Override
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if (response != null){
            try {

                dismissLoadingDialog();

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("erro")){
                    msgError("Erro na atualização do usuário");
                }
                else if (jResp.has("data")){

                    if (jResp.getString("data").contains("100")){
                        msgToastOk(this, "Atualização realizada com sucesso", true);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (validateFields()){
                    SavePassword();
                }
                break;
        }
    }

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.UPD_PASSWORD_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setListener(this);
    }

    private Boolean validateFields(){

        boolean cancel = false;
        View focusView = null;

        mInputSenha.setError(null);
        mInputConfirma.setError(null);

        String senha = mInputSenha.getText().toString().trim();
        String conf = mInputConfirma.getText().toString().trim();

        if(senha.length()<1){
            cancel = true;
            focusView = mInputSenha;
            mInputSenha.setError("Informe a senha");
        }
        else if(conf.length()<1){
            cancel = true;
            focusView = mInputConfirma;
            mInputConfirma.setError("Informe a confirmação");
        }
        else if(!senha.equals(conf)){
            cancel = true;
            focusView = mInputConfirma;
            mInputConfirma.setError("Senha não confere com a confirmação");
        }

        if(cancel) {
            focusView.requestFocus();
        }
        return !cancel;
    }

    private void SavePassword() {

        try {

            if (mGatherUser != null){

                showLoadingDialog();

                mGatherUser.setSenha( mInputSenha.getText().toString() );

                JSONObject jUser = new JSONObject();
                String strToken = General.GenerateToken(mGatherUser.getLogin());
                jUser.put(KEY_TOKEN, strToken);
                jUser.put("id", mGatherUser.getId() );
                jUser.put("senha", mGatherUser.getSenha() );

                //jUser.put(KEY_USER, mGatherUser.toString());

                mVlAdapt.setmParams(jUser);
                mVlAdapt.sendRequest();
            }
        }
        catch (Exception ex){
            msgError("Erro: " + ex.toString());
        }
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

}
