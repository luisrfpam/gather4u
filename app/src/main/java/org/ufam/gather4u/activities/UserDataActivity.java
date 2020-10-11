package org.ufam.gather4u.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.provider.MediaStore.Images.Media;

import com.android.volley.VolleyError;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.interfaces.CustomVolleyCallbackInterface;
import org.ufam.gather4u.models.User;
import org.ufam.gather4u.utils.BitmapHandle;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.listeners.CameraPermitionListener;
import org.ufam.gather4u.activities.Permitions.listeners.WriteExternalStoragePermitionListener;
import org.ufam.gather4u.activities.alert.AlertsListActivity;
import org.ufam.gather4u.activities.event.EventsListActivity;
import org.ufam.gather4u.utils.BasicImageDownloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class UserDataActivity extends RequestPermitionsActivity implements View.OnClickListener, CustomVolleyCallbackInterface {

    private static final String TAG = UserDataActivity.class.getSimpleName();
    private EditText mInputRegistration;
    private EditText mInputFullName;
    private Dialog mChoiceDialog;
    private static final int TAKE_PICTURE = 111;
    private Uri imageCameraUri;
    private Bitmap imageCamera;
    private ImageView mIvAccount;
    private String mCroppedFile;
    private boolean isNewUser = false;
    //private int userType = -1;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        Button insertNewUserButton = (Button) findViewById(R.id.insert_new_user_button);
        insertNewUserButton.setOnClickListener(this);

        Button editPictureButton = (Button) findViewById(R.id.btn_edit_image);
        editPictureButton.setOnClickListener(this);

        mInputRegistration = (EditText) findViewById(R.id.input_login);
        mInputFullName = (EditText) findViewById(R.id.input_full_name);
        mIvAccount = (ImageView) findViewById(R.id.iv_account);
        mInputFullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        if(getIntent().getExtras()!=null){
            isNewUser = getIntent().getBooleanExtra("is_new_user",false);
           // userType = getIntent().getIntExtra("usertype",-1);
        }

        ActionBar actBar = getSupportActionBar();

        if(!isNewUser){

            findViewById(R.id.btn_account).setVisibility(View.GONE);
            findViewById(R.id.btn_events).setOnClickListener(this);
            findViewById(R.id.btn_alerts).setOnClickListener(this);
            findViewById(R.id.btn_logout).setOnClickListener(this);
            findViewById(R.id.btn_location).setOnClickListener(this);


            if(actBar!=null){
                actBar.setSubtitle(getString(R.string.subtitle_tootlbar_edit_user));
            }

            mUser = getIntent().getParcelableExtra("user");
            mInputFullName.setEnabled(false);
            mInputRegistration.setEnabled(false);
            editPictureButton.setVisibility(View.GONE);
            insertNewUserButton.setVisibility(View.GONE);

            mInputFullName.setText(mUser.getNome());
            mInputRegistration.setText(mUser.getLogin());

            String path = mUser.getAvatar();

            if(path!=null){
                final File myImageFile = new File(path);
                Picasso.with(this)
                        .load(myImageFile)
                        .into(mIvAccount);
            }
        }else{
            if(actBar!=null){
                actBar.setDisplayHomeAsUpEnabled(true);
            }
            findViewById(R.id.bottom_menu_user_data).setVisibility(View.GONE);
        }

        hideKeyboard();
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

    private void bindFields(){

        boolean cancel = false;
        View focusView = null;

        mInputRegistration.setError(null);
        mInputFullName.setError(null);

        String login = mInputRegistration.getText().toString().trim();
        String nome = mInputFullName.getText().toString().trim();

        if(login.length()<1){
            cancel = true;
            focusView = mInputRegistration;
            mInputRegistration.setError("Informe um número de matrícula");
        }

        if(nome.length()<1){
            cancel = true;
            focusView = mInputFullName;
            mInputFullName.setError("Informe o nome do técnico");
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            User user = new User();
            user.setLogin(login);
            user.setNome(nome);

            if(mCroppedFile!=null){
                user.setAvatar(mCroppedFile);
            }

            Date utilDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(utilDate);
            cal.set(Calendar.MILLISECOND, 0);
            user.setDTCad(new Timestamp(utilDate.getTime()).toString());

            if(getLocalDb().insertUser(user)){
                showToast("Usuário criado com sucesso",false);
                onBackPressed();
            }else{
                showSnackBar("Houve um problema ao registrar o usário",true);
            }
        }
    }

    //METODOS DE RETORNO
    @Override
    public void deliveryResponse(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());
        //longAlert("Object Resposta: " + response.toString());

        if (response != null){
            try {
                String strResp = response.get("data").toString();

                if(strResp.indexOf("success") > -1){

                    String[] dados = strResp.split(";");

                    String strID = dados[1].toString();
                    String strLogin = dados[2].toString();
                    String strNome = dados[3].toString();
//                    General.setLogin(strID,strLogin,strNome);
//
//                    editTextUsername.setText("");
//                    editTextPassword.setText("");
//
//                    loadApollo();
                }
                else if(strResp.indexOf("failure") > -1){
                    showToast("Login Falhou!. Verifique login e senha.", true);
                }
            }
            catch (Exception e){
                showToast("Erro: " + e.toString(), true);
            }
        }
    }

    @Override
    public void deliveryResponse(JSONArray response, String flag) {
        Log.i("Script", "Array Resposta: " + response.toString());
        showToast("Array Resposta: " + response.toString(), true);
    }

    @Override
    public void deliveryError(VolleyError error, String flag) {
        Log.i("Script", "Erro: " + error);
        showToast("Erro: " + error, true);
    }

    public void takePhoto(View view) {
        String state =  Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if ( IsMarshMelloOrBefore() ) {
                File photo = new File( Environment.getExternalStorageDirectory(),  "Pic.jpg");
                imageCameraUri = Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCameraUri);
            }
            else {
                Uri mediaPath = Media.getContentUri(Environment.getExternalStorageDirectory().getAbsolutePath());
                File photo = new File(mediaPath.getPath(), "Pic.jpg");
                imageCameraUri = Uri.fromFile(photo);
            }
            startActivityForResult(intent, TAKE_PICTURE);
        }
        else
        {
            showSnackBar( "Storage não preparado: " + state, true);
        }
    }

    public void startCrop(){
        Crop.pickImage(this);
    }

    private void beginCropSquare(Uri source) {
        Uri destination = Uri.fromFile( new File(getCacheDir(), "cropped") );
        Crop.of(source, destination).asSquare().start(this);
    }

    @Override
    public void onClick(final View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_edit_image:
                choiceSourceImage();
                break;

            case R.id.insert_new_user_button:
                bindFields();
                break;

            case R.id.btn_select_camera:
                mChoiceDialog.dismiss();
                //CHAMA O CROP

                checkCameraPermition(new CameraPermitionListener() {
                    @Override
                    public void onCameraGranted() {
                        takePhoto(v);
                    }

                    @Override
                    public void onCameraDenied() {
                        showSnackBar(getResources().getString(R.string.label_snack_must_permit_camera),true);
                    }
                });
                break;

            case R.id.btn_select_galery:
                mChoiceDialog.dismiss();
                //CHAMA O CROP

                checkWriteExternalStoragePermition(new WriteExternalStoragePermitionListener() {
                    @Override
                    public void onWriteExternalStoregeGranted() {
                        startCrop();
                    }

                    @Override
                    public void onWriteExternalStoregeDenied() {
                        showSnackBar(getResources().getString(R.string.label_snack_must_permit_galery),true);
                    }
                });
                break;

            case R.id.btn_alerts:
                intent = new Intent(this, AlertsListActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;

            case R.id.btn_events:
                intent = new Intent(this, EventsListActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;

            case R.id.btn_logout:
                performLogout();
                break;

            case R.id.btn_location:
                intent = new Intent(this, GoogleApiClientActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;
        }
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

                String dateString = String.valueOf(new Date().getTime());
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "gather4u" + File.separator +"profiles"+ File.separator + dateString + "." + mFormat.name().toLowerCase());

                BasicImageDownloader.writeToDisk(myImageFile, bmp, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        mCroppedFile = myImageFile.getPath();
                        Picasso.with(UserDataActivity.this)
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

                }, mFormat, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(resultCode == Crop.RESULT_ERROR){
            String message = "Erro ao tentar cortar imagem. Tente novamente.";
            showToast(message,true);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Media.insertImage( inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //    SEMPRE QUE HA UM RETORNO A TELA A ACTIVITY, ESSE MÉTODO É CHAMADO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
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
                    showSnackBar(getResources().getString(R.string.label_snack_must_permit_galery),true);
                }
            });
        }
    }
}
