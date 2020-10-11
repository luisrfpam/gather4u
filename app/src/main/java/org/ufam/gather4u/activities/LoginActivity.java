package org.ufam.gather4u.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.GatherTables;

public class LoginActivity extends BaseActivity implements View.OnClickListener, CustomVolleyAdapterInterface {

    private final String TAG = this.getClass().getSimpleName();
    private EditText mLoginField;
    private EditText mPassWrdField;
    private TextView mLoginMsg;
    private TextView mForgoten;
    private TextView mNewUser;


    private int userType = -1;
    private JsonVolleyAdapter adapt = null;
    private GatherTables getLocData = null;
    private String strLogin = "";

    public static final String KEY_TOKEN="token";
    public static final String KEY_USERNAME="login";
    public static final String KEY_PASSWORD="senha";
    public static final String KEY_USERTYPE="tipo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginUserButton = (Button) findViewById(R.id.login_user_button);
        mLoginMsg = (TextView) findViewById(R.id.loginMsg);
        mLoginMsg.setText("");

        mForgoten = (TextView) findViewById(R.id.forgotit);
        mNewUser = (TextView) findViewById(R.id.newuser);

        mForgoten.setOnClickListener(this);
        mNewUser.setOnClickListener(this);
        loginUserButton.setOnClickListener(this);

        getLocData = new GatherTables();

        mLoginField = (EditText) findViewById(R.id.input_login);
        mPassWrdField = (EditText) findViewById(R.id.input_password);

        if(getIntent().getExtras()!=null){
            userType = getIntent().getIntExtra("usertype",-1);
            if (userType > -1){
                General.setUserType(userType);
            }
        }

        hideKeyboard();

        adapt = new JsonVolleyAdapter();
        setLoginVolleyAdapter();

    }

    @Override
    public void onStart() {
        super.onStart();
        adapt.Start();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapt.Stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissLoadingDialog();
    }

    private void setLoginVolleyAdapter(){
        String url_login = ServerInfo.SERVER_URL + "/" + ServerInfo.LOGIN_URL;
        adapt.setFunction("login");
        adapt.setURL(url_login);
        adapt.setListener(this);
    }

    private void setRememberVolleyAdapter(){
        String url_login = ServerInfo.SERVER_URL + "/" + ServerInfo.LOGIN_URL;
        adapt.setFunction("remember");
        adapt.setURL(url_login);
        adapt.setListener(this);
    }

    private Boolean validateLogin()
    {
        mLoginField.setError(null);

        strLogin = mLoginField.getText().toString().trim();

        if(strLogin.length()<1){
            mLoginField.setError("Informe o login");
            mLoginField.requestFocus();
            return false;
        }
        return true;
    }

    private Boolean validateBothFields()
    {
        boolean cancel = false;
        View focusView = null;

        mLoginField.setError(null);
        mPassWrdField.setError(null);

        strLogin = mLoginField.getText().toString().trim();
        String password = mPassWrdField.getText().toString().trim();

        if(strLogin.length()<1){
            cancel = true;
            focusView = mLoginField;
            mLoginField.setError("Informe o login");
        }

        if(password.length()<1){
            cancel = true;
            focusView = mPassWrdField;
            mPassWrdField.setError("Informe a senha");
        }

        if(cancel) {
            focusView.requestFocus();
        }
        return !cancel;
    }

    private void performLogin() {
        try {
            if (validateBothFields()){

                showLoadingDialog();

                JSONObject jobj = new JSONObject();
                //String strMD5 = md5(editTextPassword.getText().toString().trim());
                //jobj.put("password", strMD5);
                strLogin = mLoginField.getText().toString().toLowerCase().trim();
                jobj.put(KEY_USERNAME, strLogin);
                jobj.put(KEY_PASSWORD, mPassWrdField.getText().toString().toLowerCase().trim());
                jobj.put(KEY_USERTYPE, userType);
                String strToken = General.GenerateToken( strLogin );
                jobj.put(KEY_TOKEN, strToken);

                mLoginMsg.setText("");

//                String url_login = ServerInfo.SERVER_URL + "/" + ServerInfo.LOGIN_URL;
//                mVolleyConnection.callServerApiByJsonObjectRequest(url_login,
//                        "login", jobj.toString(), "login");
                setLoginVolleyAdapter();
                adapt.setmParams(jobj);
                adapt.sendRequest();
            }

        }
        catch (Exception e){
        }
    }

    private void performGetLoginData(){
        try {
            if (validateLogin()) {
                JSONObject jobj = new JSONObject();
                jobj.put(KEY_USERNAME, strLogin);
                String strToken = General.GenerateToken(strLogin);
                jobj.put(KEY_TOKEN, strToken);
                setRememberVolleyAdapter();
                adapt.setmParams(jobj);
                adapt.sendRequest();
            }
        }
        catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_user_button:
                if (isNetworkAvailable()) {
                    performLogin();
                }
                else
                {
                    msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
                }
                break;

            case R.id.forgotit:
                if (isNetworkAvailable()) {
                    performGetLoginData();
                }
                else
                {
                    msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
                }
                break;

            case R.id.newuser:
                if (isNetworkAvailable()) {
                    goToNewUser();
                }
                else
                {
                    msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
                }
                break;
        }
    }

    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if (response != null){
            try {

                dismissLoadingDialog();

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("data")) {

                    String msgResp = jResp.getString("data").toString();
                    if (    msgResp.contains("Erro:") ||
                            msgResp.contains(".php nao foi encontrado")) {
                        mLoginField.requestFocus();
                        //msgError(msgResp);
                        msgToastError(msgResp);

                    } else {

                        JSONObject jUser = new JSONObject(msgResp);

                        if (flag.equalsIgnoreCase("remember")){
                            if (jUser.has("email")) {
                                goToRememberMe(userType, strLogin, jUser.getString("senha"), jUser.getString("email"));
                            }
                        }
                        else{
                            if (jUser.has("id")) {

                                // Fill user thing ...
                                fillUserData( jUser );

                                mPassWrdField.setText("");
                                mLoginField.setText("");
                                mLoginMsg.setText("");
                                showToast("Usuário logado com sucesso!", true);
                                goToMain();
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                msgError("Erro: " + e.toString());
            }
        }
    }



    private void fillUserData( JSONObject jUser) {

        int userID = 0;

        try {

            userID = jUser.getInt("id");

        }
        catch (Exception e){
            msgError("Erro: " + e.toString());
        }

        General.setLoggedUser(jUser);

        getLocData.getUserResiduos(userID);
        getLocData.getUserRegioes(userID);
        getLocData.getUserRegioesTot(userID);
    }



    public void notifyListener(VolleyError error, String flag) {
        Log.i("Script", "Erro: " + error);
        dismissLoadingDialog();
        //showToast("Erro: " + error, true);
        msgError(error.toString());

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
