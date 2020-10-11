package org.ufam.gather4u.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.activities.Permitions.listeners.CameraPermitionListener;
import org.ufam.gather4u.activities.Permitions.listeners.WriteExternalStoragePermitionListener;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.services.JsonVolleyAdapter;
import org.ufam.gather4u.utils.BasicImageDownloader;
import org.ufam.gather4u.utils.BitmapHandle;
import org.ufam.gather4u.utils.TextMasks;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class UserColetorEditBasicosActivity
        extends RequestPermitionsActivity
        implements View.OnClickListener,
        CustomVolleyAdapterInterface {

    private JsonVolleyAdapter mVlAdapt = null;

    private EditText mInputLogin;
    private EditText mInputFullName;
    private TextView mInputCNPJ;
    private EditText mInputEmail;
    private EditText mInputWhatsApp;
    private EditText mInputTelefone;
    private EditText mInputRazaoSocial;
    private Spinner mInputTipoEmp;

    private Button mInputUpd;


    private static final int TAKE_PICTURE = 111;
    private Uri imageCameraUri;
    private Bitmap imageCamera;
    private ImageView mIvAccount;
    private String mCroppedFile;
    private Gather_User mGatherUser = null;
    private Dialog mChoiceDialog;
    private String mLoginUser = "";
    private String mSuccessUpdMsg = "";

    private Boolean mCheckLoginExiste = false;
    private Boolean mCheckEmailExiste = false;

    public static final String KEY_FUNCTION = "upd_col_basico";
    public static final String KEY_USERNAME = "login";
    public static final String KEY_TOKEN    = "token";
    public static final String KEY_USER     = "user";
    public static final String KEY_ID       = "id";
    public static final String KEY_EMAIL    ="email";
    public static final int RESULT_OK = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coletor_dados);

        if (actBar != null) {
            actBar = getSupportActionBar();
        }
        actBar.setDisplayHomeAsUpEnabled(true);

        mInputLogin = (EditText) findViewById(R.id.input_login);

        mInputFullName = (EditText) findViewById(R.id.input_fantasy_name);
        mInputFullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        mInputRazaoSocial = (EditText) findViewById(R.id.input_razao_social);
        mInputRazaoSocial.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        mInputCNPJ = (TextView) findViewById(R.id.input_cnpj);

        mInputEmail = (EditText) findViewById(R.id.input_email);

        mInputWhatsApp = (EditText) findViewById(R.id.input_whatsapp);
        mInputWhatsApp.addTextChangedListener(TextMasks.insertMobileNineGather(mInputWhatsApp));

        mInputTelefone = (EditText) findViewById(R.id.input_telefone);
        mInputTelefone.addTextChangedListener(TextMasks.insertMobileNineGather(mInputTelefone));

        mIvAccount = (ImageView) findViewById(R.id.iv_account);

        Button editPictureButton = (Button) findViewById(R.id.btn_edit_image);
        editPictureButton.setOnClickListener(this);

        mInputTipoEmp = (Spinner) findViewById(R.id.input_tipo_emp);

        mInputLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    performCheckLoginData();
            }
        });

        mInputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    performCheckEmail();
            }
        });

        mInputUpd = (Button) findViewById(R.id.btn_update);
        mInputUpd.setOnClickListener(this);

        mVlAdapt = new JsonVolleyAdapter();

        hideKeyboard();
    }

    private void fillUserDataFields(){
        mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );

        mInputFullName.setText( mGatherUser.getNome() );
        mLoginUser = mGatherUser.getLogin();
        mInputLogin.setText( mLoginUser );
        mInputCNPJ.setText( mGatherUser.getCnpj() );
        mInputEmail.setText( mGatherUser.getEmail() );
        mInputWhatsApp.setText( mGatherUser.getWhatsapp() );
        mInputWhatsApp.endBatchEdit();
        mInputTelefone.setText( mGatherUser.getTelefone() );
        mInputTelefone.endBatchEdit();
        mInputRazaoSocial.setText( mGatherUser.getRazaosocial() );

        int empresaIdx = mGatherUser.getTipoempresa();
        mInputTipoEmp.setSelection( empresaIdx - 1 );

        String strfoto = mGatherUser.getAvatar();
        if (strfoto != null){
            if (strfoto.trim().length() > 0 ){
                BitmapHandle imageHandler = new BitmapHandle();
                imageHandler.loadImage( this, mIvAccount, mLoginUser, strfoto);
            }
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
        super.onStop();
        mVlAdapt.Stop();
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
    public void onClick(final View view) {

        switch (view.getId()){
            case R.id.btn_edit_image:
                if (validadeLogin()){
                    choiceSourceImage();
                }
                break;

            case R.id.btn_update:
                if (validadeFields()){
                    setSaveDadosVolleyAdapter();
                    UpdateUser();
                }
                break;

            case R.id.btn_select_camera:
                mChoiceDialog.dismiss();
                //CHAMA O CROP

                checkCameraPermition(new CameraPermitionListener() {
                    @Override
                    public void onCameraGranted() {
                        takePhoto(view);
                    }

                    @Override
                    public void onCameraDenied() {
                        msgToastInfo(getResources().getString(R.string.label_snack_must_permit_camera));
                    }
                });
                break;

            case R.id.btn_select_galery:
                mChoiceDialog.dismiss(); //CHAMA O CROP
                checkWriteExternalStoragePermition(new WriteExternalStoragePermitionListener() {
                    @Override
                    public void onWriteExternalStoregeGranted() {
                        startCrop();
                    }

                    @Override
                    public void onWriteExternalStoregeDenied() {
                        msgToastInfo(getResources().getString(R.string.label_snack_must_permit_galery));
                    }
                });
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        // perform your action here
        if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            //DEPOIS QUE O USÁRIO ESCOLHE A IMAGEM NA GALERIA, ELE VEM PRA CA PRA INICIAR O CROP.
            beginCropSquare(result.getData()); //INICI O CROP NO MODO QUADRADO
        }
        else if(requestCode == Crop.REQUEST_CROP){
            //DEPOIS QUE O USÁRIO FAZ O CROP, SERÁ INICIADO O TRATAMENTO DA IMAGEM.
            handleCrop(resultCode, result);
        }
        else if(requestCode == TAKE_PICTURE && resultCode == RESULT_OK){

            if ( !IsMarshMelloOrBefore() ) {
                Bundle extras = result.getExtras();
                imageCamera = (Bitmap) extras.get("data");

                if (imageCamera != null) {
                    imageCameraUri = getImageUri(this, imageCamera);
                }
            }

            checkWriteExternalStoragePermition(new WriteExternalStoragePermitionListener() {
                @Override
                public void onWriteExternalStoregeGranted() {
                    beginCropSquare(imageCameraUri);
                }

                @Override
                public void onWriteExternalStoregeDenied() {
                    msgToastInfo(getResources().getString(R.string.label_snack_must_permit_galery));
                }
            });
        }
    }

    public void takePhoto(View view) {
        String state =  Environment.getExternalStorageState();

        String imgName = mInputLogin.getText().toString().trim();

        if (Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if ( IsMarshMelloOrBefore() ) {
                File photo = new File( Environment.getExternalStorageDirectory(),  imgName + ".jpg");
                imageCameraUri = Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCameraUri);
            }
            else {
                Uri mediaPath = MediaStore.Images.Media.getContentUri(Environment.getExternalStorageDirectory().getAbsolutePath());
                File photo = new File(mediaPath.getPath(), imgName + ".jpg");
                imageCameraUri = Uri.fromFile(photo);
            }
            startActivityForResult(intent, TAKE_PICTURE);
        }
        else
        {
            msgError( "Storage não preparado: " + state);
        }
    }

    public void startCrop(){
        Crop.pickImage(this);
    }

    private void beginCropSquare(Uri source) {
        Uri destination = Uri.fromFile( new File(getCacheDir(), "cropped") );
        Crop.of(source, destination).asSquare().start(this);
    }

    public void choiceSourceImage(){

        mChoiceDialog = new Dialog(this, R.style.FullHeightDialog);
        mChoiceDialog.setContentView(R.layout.dialog_select_source_image);
        Button selectCamera = (Button) mChoiceDialog.findViewById(R.id.btn_select_camera);
        Button selectGalery = (Button) mChoiceDialog.findViewById(R.id.btn_select_galery);

        if(selectCamera!=null){
            selectCamera.setOnClickListener(this);
        }

        if(selectGalery!=null){
            selectGalery.setOnClickListener(this);
        }

        mChoiceDialog.show();
    }

    private void handleCrop(int resultCode, Intent result) {
        if(resultCode == RESULT_OK){
            try {
                String cropPath = Crop.getOutput(result).getPath();
                Bitmap bmp = null;
                /// Erro MOTOG 5
                if ( IsMarshMelloOrBefore() ) {
                    bmp = new BitmapHandle().resizeCustom(cropPath,720,720);
                    bmp = BitmapHandle.rotateImageIfRequired(bmp,Uri.parse( cropPath ));
                }
                else {
                    if ( imageCamera != null ) {
                        bmp = imageCamera;
                    }
                }

                SaveImageToDiskAndLoad(bmp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(resultCode == Crop.RESULT_ERROR){
            String message = "Erro ao tentar cortar imagem. Tente novamente.";
            msgError(message);
        }
    }

    private void SaveImageToDiskAndLoad(Bitmap bmp){

        //String dateString = String.valueOf(new Date().getTime());

        final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
        final File myImageFile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "gather4u" + File.separator +
                        "profiles"+ File.separator + mLoginUser + "." +
                        mFormat.name().toLowerCase());

        BasicImageDownloader.writeToDisk(myImageFile, bmp,
                new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        mCroppedFile = myImageFile.getPath();
                        Picasso.with(General.getAppContext())
                                .load(myImageFile)
                                .memoryPolicy(MemoryPolicy.NO_CACHE )
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(mIvAccount);
                    }

                    @Override
                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                        Log.e(TAG, "Error code " + error.getErrorCode() + ": " +error.getMessage());
                        error.printStackTrace();
                    }

                }, mFormat, true);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage( inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void performCheckLoginData(){
        try {
            String login  = mInputLogin.getText().toString().trim();
            String userlogin  = mGatherUser.getLogin();
            if(login.length()> 0){
                // Previne a exibição da verificação para a própria senha do usuário logado.
                if (!login.equalsIgnoreCase(userlogin)) {
                    mCheckLoginExiste = false;
                    JSONObject jobj = new JSONObject();
                    jobj.put(KEY_USERNAME, login);
                    String strToken = General.GenerateToken(login);
                    jobj.put(KEY_TOKEN, strToken);
                    setVerifyLoginVolleyAdapter();
                    mVlAdapt.setmParams(jobj);
                    mVlAdapt.sendRequest();
                }
            }
        }
        catch (Exception e) {
        }
    }

    private void performCheckEmail(){
        try {
            String email  = mInputEmail.getText().toString().trim();
            if(email.length()> 0){
                showLoadingDialog();

                mCheckEmailExiste = false;
                JSONObject jobj = new JSONObject();

                String login  = mInputLogin.getText().toString().trim();
                String strToken = General.GenerateToken(login);

                jobj.put(KEY_TOKEN, strToken);
                jobj.put(KEY_EMAIL, email);
                setVerifyEmailVolleyAdapter();
                mVlAdapt.setmParams(jobj);
                mVlAdapt.sendRequest();
            }
        }
        catch (Exception e) {
        }
    }

    private void setVerifyLoginVolleyAdapter(){
        String url_login = ServerInfo.SERVER_URL + "/" + ServerInfo.LOGIN_URL;
        mVlAdapt.setFunction("remember");
        mVlAdapt.setURL(url_login);
        mVlAdapt.setListener(this);
    }

    private void setVerifyEmailVolleyAdapter(){
        String url_email = ServerInfo.SERVER_URL + "/" + ServerInfo.CHECKFIELDS_URL;
        mVlAdapt.setFunction("upd_checkemail");
        mVlAdapt.setURL(url_email);
        mVlAdapt.setListener(this);
    }

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.UPD_USUARIOS_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
    }

    @Override
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if (response != null){
            try {

                dismissLoadingDialog();

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("data")) {
                    String msgResp = jResp.getString("data").toString();
                    if (msgResp.contains("100")){
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
                    else if (msgResp.contains(".php nao foi encontrado")) {
                        msgError(msgResp);
                    }
                    else {
                        if (flag.equalsIgnoreCase("remember") && msgResp.indexOf("Erro") == -1) {
                            JSONObject resp = new JSONObject(jResp.getString("data"));
                            if (resp.has("email")) {
                                String login =  mInputLogin.getText().toString().trim();
                                String msgErro = String.format(
                                        "O login '%s' já foi cadastrado no sistema. Informe Outro!", login);
                                msgError(msgErro);
                                //mInputLogin.setText("");
                                mCheckLoginExiste = true;
                            }
                        }
                        else if ( flag.equalsIgnoreCase("upd_checkemail") ) {
                            if (msgResp.contains("102")){
                                mCheckEmailExiste = false;
                            }
                        }
                    }
                }
                else if (jResp.has("erro")) {
                    if ( flag.equalsIgnoreCase("upd_checkemail") ) {

                        String email =  mInputEmail.getText().toString().trim();
                        String msgErro = String.format(
                                "O E-mail '%s' já foi cadastrado. Informe Outro!", email);
                        msgError(msgErro);
                        mCheckEmailExiste = true;
                    }
                    else if (jResp.has("erro")) {
                        msgError(jResp.getString("erro"));
                    }
                }
            }
            catch (Exception e){
                msgError("Erro: " + e.toString());
            }
        }
    }

    @Override
    public void notifyListener(VolleyError erro, String flag) {
        Log.i("Script", "Erro: " + erro);
        dismissLoadingDialog();
    }

    private Boolean validadeFields(){

        boolean cancel = false;
        View focusView = null;

        mInputFullName.setError(null);
        mInputLogin.setError(null);
        mInputEmail.setError(null);
        mInputWhatsApp.setError(null);
        mInputTelefone.setError(null);
        mInputRazaoSocial.setError(null);

        String nome = mInputFullName.getText().toString().trim();
        String login = mInputLogin.getText().toString().trim();
        String email = mInputEmail.getText().toString().trim();
        String whats = mInputWhatsApp.getText().toString().trim();
        String razao = mInputRazaoSocial.getText().toString().trim();
        String tel = mInputTelefone.getText().toString().trim();

        TextView tipoemp = (TextView) mInputTipoEmp.getSelectedView();
        tipoemp.setError(null);

        if(nome.length()<1){
            cancel = true;
            focusView = mInputFullName;
            mInputFullName.setError("Informe o nome fantasia");
        }
        else if(razao.length()<1){
            cancel = true;
            focusView = mInputRazaoSocial;
            mInputRazaoSocial.setError("Informe a razão social");
        }
        else if(login.length()<1){
            cancel = true;
            focusView = mInputLogin;
            mInputLogin.setError("Informe o login");
        }
        else if(mCheckLoginExiste){
            cancel = true;
            focusView = mInputLogin;
            String msgErro = String.format(
                    "O login '%s' já foi cadastrado no sistema. Informe Outro!", login);
            mInputLogin.setError(msgErro);
        }
        else if(email.length()<1){
            cancel = true;
            focusView = mInputEmail;
            mInputEmail.setError("Informe o e-mail");
        }
        else if(!TextMasks.isValidEmail(email)){
            cancel = true;
            focusView = mInputEmail;
            mInputEmail.setError("E-mail inválido");
        }
        else if (mCheckEmailExiste){
            cancel = true;
            String msgErro = String.format("O E-mail '%s' já foi cadastrado. Informe Outro!", email);
            focusView = mInputEmail;
            mInputEmail.setError(msgErro);
        }
        else if(whats.length()<1){
            cancel = true;
            focusView = mInputWhatsApp;
            mInputWhatsApp.setError("Informe o nr. whatsapp");
        }
        else if(whats.replace("-","").length()<11){
            cancel = true;
            focusView = mInputWhatsApp;
            mInputWhatsApp.setError("Nr. whatsapp inválido");
        }
        else if(tel.length()<1){
            cancel = true;
            focusView = mInputTelefone;
            mInputTelefone.setError("Informe o nr. telefone");
        }
        else if(tel.replace("-","").length()<11){
            cancel = true;
            focusView = mInputTelefone;
            mInputTelefone.setError("Nr. telefone inválido");
        }
        else if (tipoemp.length() < 1){
            tipoemp.setTextColor(Color.RED);//just to highlight that this is an error
            tipoemp.setText("Informe o tipo de empresa");//changes the selected item text to this
        }

        if(cancel) {
            focusView.requestFocus();
        }

        return !cancel;
    }

    private Boolean validadeLogin() {

        mInputLogin.setError(null);
        String login = mInputLogin.getText().toString().trim();
        if(login.length()<1){
            mInputLogin.requestFocus();
            mInputLogin.setError("Informe o login");
            return false;
        }
        return true;
    }

    private void UpdateUser() {

        try {
            if (mGatherUser != null) {

                showLoadingDialog();

                String nome = mInputFullName.getText().toString().trim();
                String razao = mInputRazaoSocial.getText().toString().trim();
                String login = mInputLogin.getText().toString().trim();
                String email = mInputEmail.getText().toString().trim();
                String whats = mInputWhatsApp.getText().toString().trim();
                String tele = mInputTelefone.getText().toString().trim();
//                String cnpj = mInputCNPJ.getText().toString().trim();
                int idTipoEmp = mInputTipoEmp.getSelectedItemPosition() + 1;

                mGatherUser.setNome(nome);
                mGatherUser.setLogin(login);
                mGatherUser.setEmail(email);
                mGatherUser.setWhatsapp(whats);
                mGatherUser.setTelefone(tele);
                mGatherUser.setTipoPessoa((short) 1);
                //mGatherUser.setCnpj(cnpj);
                mGatherUser.setTipoEmpresa( (short)idTipoEmp );
                mGatherUser.setTipoEmpresa( (short)idTipoEmp );
                mGatherUser.setRazaoSocial( razao );

                if(mCroppedFile!=null){
                    try {
                        BitmapHandle bh = new BitmapHandle();
                        String imgByteArray = bh.getBase64File( Uri.parse(mCroppedFile) );
                        mGatherUser.setAvatar(imgByteArray);
                    }
                    catch (Exception ex){
                    }
                }

                mSuccessUpdMsg = "\n\r" +
                        mGatherUser.getNome() + "\n\r" +
                        "Cadastro atualizado com sucesso. \n\r" +
                        "\n\r" +
                        "Dados de login: \n\r" +
                        "Login: " + mGatherUser.getLogin() + "\n\r" +
                        "Senha: " + mGatherUser.getSenha() + "\n\r" +
                        "Acesse agora mesmo.";
            }

            JSONObject jUser = new JSONObject();
            String strToken = General.GenerateToken(mGatherUser.getLogin());
            jUser.put(KEY_TOKEN, strToken);
            jUser.put(KEY_USER, mGatherUser.toString());

            mVlAdapt.setmParams(jUser);
            mVlAdapt.sendRequest();

        } catch (Exception e) {
        }

    }

}
