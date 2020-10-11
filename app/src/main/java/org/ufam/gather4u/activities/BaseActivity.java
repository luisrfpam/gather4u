package org.ufam.gather4u.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.main.MainActivity;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.VolleyConnectionQueue;
import org.ufam.gather4u.storage.DBAccess;
import org.ufam.gather4u.storage.PrefManager;

public class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    protected ActionBar actBar = null;
    protected Dialog mMsgDialog = null;
    private ProgressDialog progress;
    protected int[] mDlgImgs = null;

    protected Button btnOK =  null;
    protected Button btnSim = null;
    protected Button btnNao = null;
    protected Button btnCancel = null;
    protected EditText mMsg = null;

    public Constants.MsgResult mDlgResult = Constants.MsgResult.OK;

    public static final int DLG_DISPLAY_TIME = 3500;
    public static LocationManager mManager = null;
    public static ConnectivityManager mConnectivity = null;

    /*
        AQUI VOCE INSTANCIA A CLASSE DE CONEXAO.
        CADA ACTIVITY DEVE TER SUA INSTANCIA
        * */
    //protected VolleyConnection mVolleyConnection = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //protected GoogleApiClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actBar = getSupportActionBar();
        if (actBar != null){
            actBar.setDisplayHomeAsUpEnabled(true);
        }
        mManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE );

        mDlgImgs = new int[] { R.drawable.ok, R.drawable.info, R.drawable.error,
                               R.drawable.exclamation, R.drawable.question };
        /*
        INICIA A FILA DE REQUISIÇÕES HTTP
        ESSA FILA ESTA DENTRO DE UMA CLASSE SINGLETON, POIS DEVE HAVER APENAS UMA FILA DE REQUISIÇÕES
        PARA TODA A APLICAÇÃO

        COMO TODAS AS ACTIVITYS HERDAM DE BASE ACTIVITY, TODAS TEM A MESMA FILA DE REQUISIÇÕES
         */
        VolleyConnectionQueue.getINSTANCE().startQueue(this);

        getLocalDb();
    }

    public DBAccess getLocalDb(){
        return DBAccess.getInstance(this);
    }

    public void hideKeyboard(){
        try{
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideKeyboard(Context context){
        try{
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showSnackBar(String text,boolean isShowLong){
        if(isShowLong){
            Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showToast(String msg, boolean isShowLong) {
        if(isShowLong){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    protected void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected void goToLocation(){
        Intent intent = new Intent(this, UserLocationActivity.class);
        startActivity(intent);
    }

    protected void goToUpdPassword(){
        Intent intent = new Intent(this, UserUpdatePasswordActivity.class);
        startActivity(intent);
    }

    protected void goToNovaEntrega(){
        Intent intent = new Intent(this, NovaEntregaActivity.class);
        startActivity(intent);
    }

    protected void goToLogin(int userType){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("usertype", userType);
        startActivity(intent);
    }

    protected void goToUpdResiduos(){
        Intent intent = new Intent(this, UserColetorEditResiduoActivity.class);
        startActivity(intent);
    }

    protected void goToUpdRegioes(){
        Intent intent = new Intent(this, UserColetorEditRegiaoActivity.class);
        startActivity(intent);
    }

    protected void goEditDados(){
        Intent intent = null;
        switch (General.getUserType()){
            case 2:
                //actBar.setSubtitle( Html.fromHtml("<font color='#FFBF00'>" + getText(R.string.button_user_participante) + "</font>") );
                intent = new Intent(this, UserEntregadorEditBasicosActivity.class);
                break;
            case 3:
                //actBar.setSubtitle( Html.fromHtml("<font color='#FFBF00'>" + getText(R.string.button_user_coletor) + "</font>") );
                intent = new Intent(this, UserColetorEditBasicosActivity.class);
                break;
        }
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void goEditEndereco(){
        Intent intent = null;
        switch (General.getUserType()){
            case 2:
                //actBar.setSubtitle( Html.fromHtml("<font color='#FFBF00'>" + getText(R.string.button_user_participante) + "</font>") );
                intent = new Intent(this, UserEntregadorEditEnderecoActivity.class);
                break;
            case 3:
                //actBar.setSubtitle( Html.fromHtml("<font color='#FFBF00'>" + getText(R.string.button_user_coletor) + "</font>") );
                intent = new Intent(this, UserEntregadorEditEnderecoActivity.class);
                break;
        }
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void goToUserType(){
        Intent intent = new Intent(this, UserTypeChoiceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("is_new_user",false);
        startActivity(intent);
    }

    protected void goToNewUser(){
        Intent intent = new Intent(this, UserTypeChoiceActivity.class);
        intent.putExtra("is_new_user",true);
        startActivity(intent);
    }

    protected void goToRememberMe(int userType, String login, String senha, String email){
        Intent intent = new Intent(this, RememberLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("usertype", userType);
        intent.putExtra("login", login);
        intent.putExtra("senha", senha);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    protected void performLogout(){
        PrefManager.getINSTANCE(this).clearSession();
        goToMain();
    }

    protected void closeApp(){
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("close_app", true);
        startActivity(intent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void showToastMsg(Context context, String title, String message, Constants.ImageType type){

        showToastMsg(context, title, message, type, false);
    }

    public void showToastMsg(Context context, String title, String message, Constants.ImageType type, final Boolean doBack){

        try {

            if (mMsgDialog != null ) { mMsgDialog.dismiss(); }
            mMsgDialog = new Dialog(context, R.style.FullHeightDialog);
            mMsgDialog.setContentView(R.layout.dialog_generic_message);
            TextView msgTitle = (TextView) mMsgDialog.findViewById(R.id.title);
            TextView msg = (TextView) mMsgDialog.findViewById(R.id.message);
            ImageView img = (ImageView) mMsgDialog.findViewById(R.id.dlgImage);

            btnOK = (Button) mMsgDialog.findViewById(R.id.btn_ok);
            btnOK.setVisibility( View.GONE );

            btnSim = (Button) mMsgDialog.findViewById(R.id.btn_yes);
            btnSim.setVisibility( View.GONE );

            btnNao = (Button) mMsgDialog.findViewById(R.id.btn_no);
            btnNao.setVisibility( View.GONE );

            msgTitle.setText(title);
            msg.setText(message);
            img.setImageResource(mDlgImgs[type.getValor()]);
            mMsgDialog.show();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        if (mMsgDialog != null) {
                            Thread.sleep(DLG_DISPLAY_TIME);
                            mMsgDialog.dismiss();
                            if (doBack) {
                                onBackPressed();
                            }
                        }
                    }
                    catch (Exception ex) {

                    }
                }
            }, DLG_DISPLAY_TIME);
        }
        catch (Exception ex){
        }
    }

    public void showDlgData(Context context, String title){

        if (mMsgDialog != null ) { mMsgDialog.dismiss(); }

        mMsgDialog = new Dialog(this, R.style.FullHeightDialog);
        mMsgDialog.setContentView(R.layout.dialog_calendar);
        TextView msgTitle = (TextView) mMsgDialog.findViewById(R.id.title);

        btnOK = (Button) mMsgDialog.findViewById(R.id.btn_select);
        //btnOK.setVisibility( View.VISIBLE );

        msgTitle.setText(title);
        setClick(btnOK);

        mMsgDialog.show();
        mMsg.requestFocus();
    }

    public void showDlgMotivo(Context context, String title){

        if (mMsgDialog != null ) { mMsgDialog.dismiss(); }

        mMsgDialog = new Dialog(this, R.style.FullHeightDialog);
        mMsgDialog.setContentView(R.layout.dialog_motivo_cancel);
        TextView msgTitle = (TextView) mMsgDialog.findViewById(R.id.title);
        mMsg = mMsgDialog.findViewById(R.id.input_motivo);

        btnOK = (Button) mMsgDialog.findViewById(R.id.btn_motivo_ok);
        //btnOK.setVisibility( View.VISIBLE );

        btnCancel = (Button) mMsgDialog.findViewById(R.id.btn_cancel);
        //btnCancel.setVisibility( View.VISIBLE );

        msgTitle.setText(title);
        setClick(btnOK);
        setClick(btnCancel);

        mMsgDialog.show();
        mMsg.requestFocus();
    }

    public void showMsg(Context context, String title, String message, Constants.ImageType type){

        if (mMsgDialog != null ) { mMsgDialog.dismiss(); }
        mMsgDialog = new Dialog(this, R.style.FullHeightDialog);
        mMsgDialog.setContentView(R.layout.dialog_generic_message);
        TextView msgTitle = (TextView) mMsgDialog.findViewById(R.id.title);
        TextView msg = (TextView) mMsgDialog.findViewById(R.id.message);
        ImageView img = (ImageView) mMsgDialog.findViewById(R.id.dlgImage);

        btnOK = (Button) mMsgDialog.findViewById(R.id.btn_ok);
        btnOK.setVisibility( View.GONE );

        btnSim = (Button) mMsgDialog.findViewById(R.id.btn_yes);
        btnSim.setVisibility( View.GONE );

        btnNao = (Button) mMsgDialog.findViewById(R.id.btn_no);
        btnNao.setVisibility( View.GONE );

        msgTitle.setText(title);
        msg.setText(message);
        img.setImageResource(mDlgImgs[type.getValor()]);

        setClick(btnOK);
        setClick(btnSim);
        setClick(btnNao);

        switch (type){
            case QUESTION:
                btnSim.setVisibility( View.VISIBLE );
                btnNao.setVisibility( View.VISIBLE );
                break;

            case OK:
            case INFO:
            case ERROR:
            case EXCLAMATION:
                btnSim.setVisibility( View.GONE );
                btnNao.setVisibility( View.GONE );
                btnOK.setVisibility( View.VISIBLE );
                break;
        }
        mMsgDialog.show();
    }

    public void setClick(Button btn){
        if (btn != null){
           btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   switch (view.getId()) {
                       case R.id.btn_ok:
                       case R.id.btn_motivo_ok:
                       case R.id.btn_select:
                           mDlgResult = Constants.MsgResult.OK;
                           break;

                       case R.id.btn_yes:
                           mDlgResult = Constants.MsgResult.YES;

                           break;

                       case R.id.btn_no:
                           mDlgResult = Constants.MsgResult.NO;
                           break;

                       case R.id.btn_cancelar:
                       case R.id.btn_cancel:
                           mDlgResult = Constants.MsgResult.CANCEL;
                           break;
                   }
                   mMsgDialog.dismiss();
                   processDlgReturn();
               }
           });
        }
    }

    public void processDlgReturn() { }

    public void msgError(String message){ msgError(this,  message); }

    public void msgInfo(String message){ msgInfo(this, message ); }

    public void msgWarning(String message){ msgWarning(this, message); }

    public void msgQuestion(String message){ msgQuestion(this, message ); }


    public void msgError(Context context, String message){ showMsg(context, "Erro", message, Constants.ImageType.ERROR); }

    public void msgInfo(Context context, String message){ showMsg(context,"Informação", message, Constants.ImageType.INFO); }

    public void msgWarning(Context context,String message){ showMsg(context,"Atenção", message, Constants.ImageType.EXCLAMATION); }

    public void msgQuestion(Context context, String message){ showMsg(context,"Responda", message, Constants.ImageType.QUESTION); }


    public void msgToastOk(String message){ showToastMsg( this, "Sucesso", message, Constants.ImageType.OK); }

    public void msgToastInfo(String message){ msgToastInfo(  this, message); }

    public void msgToastError(String message){ msgToastError(  this, message); }


    public void msgToastOk(Context context, String message, Boolean doBack){ showToastMsg( this, "Sucesso", message, Constants.ImageType.OK, true); }

    public void msgToastOk(Context context, String message){ showToastMsg( this, "Sucesso", message, Constants.ImageType.OK); }

    public void msgToastInfo(Context context,String message){ showToastMsg(  this,"Informação", message, Constants.ImageType.INFO); }

    public void msgToastError(Context context, String message){ showToastMsg(  this,"Erro", message, Constants.ImageType.ERROR); }

    public void msgToastError(Context context, String message, Boolean doBack){ showToastMsg( this, "Erro", message, Constants.ImageType.ERROR, true); }


    // ############################################################################
    // ######################### Loadind Dialog ###################################
    // ############################################################################
    public void showLoadingDialog(String msg) {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setTitle("Aguarde");

            if ( msg.isEmpty() ){
                msg = "Carregando ...";
            }

            progress.setMessage(msg);
        }
        progress.show();
    }

    public void showLoadingDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setTitle("Aguarde");
            progress.setMessage("Carregando ...");
        }
        progress.show();
    }

    public void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public boolean isNetworkAvailable() {

        mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivity != null) {
            NetworkInfo[] info = mConnectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean isGPSAvailable(){

       return mManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
    }

    public Boolean isMsgGPSAvailable(){
        Boolean res = isGPSAvailable();
        if (!res){
            msgToastError("GPS is disabled!");
        }
        return res;
    }

    protected Boolean IsMarshMelloOrBefore(){
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.M;
    }

    protected Boolean IsOverMarshMello()
    {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}
