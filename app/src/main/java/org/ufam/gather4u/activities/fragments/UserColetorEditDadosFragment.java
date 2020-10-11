package org.ufam.gather4u.activities.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.ufam.gather4u.activities.UserColetorEditActivity;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.eventbus.ColetorEventMessage;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.utils.BasicImageDownloader;
import org.ufam.gather4u.utils.BitmapHandle;
import org.ufam.gather4u.utils.TextMasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserColetorEditDadosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserColetorEditDadosFragment extends BaseFragment
        implements View.OnClickListener, CustomVolleyAdapterInterface,
        BaseFragment.OnFragmentInteractionListener{

    private EditText mInputLogin;
    private EditText mInputFullName;
    private TextView mInputCNPJ;
    private EditText mInputEmail;
    private EditText mInputWhatsApp;
    private EditText mInputTelefone;
    private EditText mInputRazaoSocial;

    private Spinner mInputTipoEmp;
    private Button mInputNext;

    private static final int TAKE_PICTURE = 111;
    private Uri imageCameraUri;
    private Bitmap imageCamera;
    private ImageView mIvAccount;
    private String mCroppedFile;
    private Gather_User mGatherUser = null;
    private String mLoginUser = "";

    public static final String KEY_USERNAME="login";
    public static final String KEY_TOKEN="token";
    public static final int RESULT_OK = -1;
    private Dialog mChoiceDialog;

    private Boolean mCheckLoginExiste = false;

    public UserColetorEditDadosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * @return A new instance of fragment CustomFragment.
     */
    public static UserColetorEditDadosFragment newInstance() {
        UserColetorEditDadosFragment fragment = new UserColetorEditDadosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_edit_coletor_dados, container, false);

        mInputLogin = (EditText) v.findViewById(R.id.input_login);
        mInputFullName = (EditText) v.findViewById(R.id.input_fantasy_name);
        mInputFullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        mInputRazaoSocial = (EditText) v.findViewById(R.id.input_razao_social);
        mInputRazaoSocial.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        mInputCNPJ = (TextView) v.findViewById(R.id.input_cnpj);

        mInputEmail = (EditText) v.findViewById(R.id.input_email);
        mInputWhatsApp = (EditText) v.findViewById(R.id.input_whatsapp);
        mInputWhatsApp.addTextChangedListener(TextMasks.insertMobileNineGather(mInputWhatsApp));

        mInputTelefone = (EditText) v.findViewById(R.id.input_telefone);
        mInputTelefone.addTextChangedListener(TextMasks.insertMobileNineGather(mInputTelefone));


        mIvAccount = (ImageView) v.findViewById(R.id.iv_account);
        Button editPictureButton = (Button) v.findViewById(R.id.btn_edit_image);
        editPictureButton.setOnClickListener(this);

        mInputTipoEmp = (Spinner) v.findViewById(R.id.input_tipo_emp);

        mInputLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    performCheckLoginData();
            }
        });

        mInputNext = (Button) v.findViewById(R.id.btn_next);
        mInputNext.setOnClickListener(this);

        fillUserDataFields();

        return v;
    }

    private void fillUserDataFields(){
        mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );

        mInputFullName.setText( mGatherUser.getNome() );
        mInputRazaoSocial.setText( mGatherUser.getRazaosocial() );
        mLoginUser = mGatherUser.getLogin();
        mInputLogin.setText( mLoginUser );
        mInputCNPJ.setText( mGatherUser.getCnpj() );
        mInputEmail.setText( mGatherUser.getEmail() );
        mInputWhatsApp.setText( mGatherUser.getWhatsapp() );
        mInputTelefone.setText( mGatherUser.getTelefone() );

        int tipoempresaIdx = mGatherUser.getTipoempresa();
        mInputTipoEmp.setSelection( tipoempresaIdx - 1 );

        String strfoto = mGatherUser.getAvatar();
        if (strfoto != null){
            if (strfoto.trim().length() > 0 ){
                BitmapHandle imageHandler = new BitmapHandle();
                imageHandler.loadImage( mActParent, mIvAccount, mLoginUser, strfoto);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()){
            case R.id.btn_edit_image:
                if (validateLogin()){
                    choiceSourceImage();
                }
                break;

            case R.id.btn_next:
                if (validadeFields()){
                    SendDataToEnderecoFragment();
                    ((UserColetorEditActivity)mActParent).moveNextFragment();
                }
                break;

            case R.id.btn_select_camera:
                mChoiceDialog.dismiss();
                //CHAMA O CROP

                ((RequestPermitionsActivity)mActParent).checkCameraPermition(new CameraPermitionListener() {
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
                ((RequestPermitionsActivity)mActParent).checkWriteExternalStoragePermition(new WriteExternalStoragePermitionListener() {
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

    private void SendDataToEnderecoFragment() {

        String nome = mInputFullName.getText().toString().trim();
        String razao = mInputRazaoSocial.getText().toString().trim();
        String login = mInputLogin.getText().toString().trim();
        String email = mInputEmail.getText().toString().trim();
        String whats = mInputWhatsApp.getText().toString().trim();
        String tele = mInputTelefone.getText().toString().trim();
        int tipoemp = mInputTipoEmp.getSelectedItemPosition() + 1;

        mGatherUser.setNome(nome);
        mGatherUser.setRazaoSocial(razao);
        mGatherUser.setLogin(login);
        mGatherUser.setEmail(email);
        mGatherUser.setWhatsapp(whats);
        mGatherUser.setTelefone(tele);
        mGatherUser.setTipoPessoa((short) 1);
        mGatherUser.setTipoEmpresa((short) tipoemp);

        if(mCroppedFile!=null){
            try {
                BitmapHandle bh = new BitmapHandle();
                String imgByteArray = bh.getBase64File( Uri.parse(mCroppedFile) );
                mGatherUser.setAvatar(imgByteArray);
            }
            catch (Exception ex){
            }
        }

        EventPost( new ColetorEventMessage(mGatherUser, null, null, this.TAG) );
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

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
            showToastMsg( "Erro", "Storage não preparado: " + state, Constants.ImageType.ERROR);
        }
    }

    public void startCrop(){
        Crop.pickImage(mActParent);
    }

    private void beginCropSquare(Uri source) {
        Uri destination = Uri.fromFile( new File(mActParent.getCacheDir(), "cropped") );
        Crop.of(source, destination).asSquare().start(mActParent, this, Crop.REQUEST_CROP);
    }

    public void choiceSourceImage(){

        mChoiceDialog = new Dialog(mActParent, R.style.FullHeightDialog);
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
                        Picasso.with(mActParent)
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
            msgError(message);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage( inContext.getContentResolver(), inImage, "Title", null);
        if (path != null){
            return Uri.parse(path);
        }
        return Uri.EMPTY;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        //super.onActivityResult(requestCode, resultCode, result);
        // perform your action here
        final int unmaskedRequestCode = requestCode & 0x0000ffff;

        if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            //DEPOIS QUE O USÁRIO ESCOLHE A IMAGEM NA GALERIA, ELE VEM PRA CA PRA INICIAR O CROP.
            beginCropSquare(result.getData()); //INICI O CROP NO MODO QUADRADO
        }
        else if(requestCode == Crop.REQUEST_CROP){
            //DEPOIS QUE O USÁRIO FAZ O CROP, SERÁ INICIADO O TRATAMENTO DA IMAGEM.
            handleCrop(resultCode, result);
        }
        else if( ( requestCode == TAKE_PICTURE || unmaskedRequestCode == TAKE_PICTURE ) && resultCode == RESULT_OK){

            if ( !IsMarshMelloOrBefore() ) {
                Bundle extras = result.getExtras();
                imageCamera = (Bitmap) extras.get("data");

                if (imageCamera != null) {
                    imageCameraUri = getImageUri(getContext(), imageCamera);
                }
            }

            ((RequestPermitionsActivity)mActParent).checkWriteExternalStoragePermition(new WriteExternalStoragePermitionListener() {
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

    private Boolean validateLogin()
    {
        mInputLogin.setError(null);
        String login  = mInputLogin.getText().toString().trim();
        if(login.length()<1){
            mInputLogin.setError("Informe o login");
            mInputLogin.requestFocus();
            return false;
        }
        return true;
    }

    private void performCheckLoginData(){
        try {
            String login  = mInputLogin.getText().toString().trim();
            String userlogin  = mGatherUser.getLogin();
            if(login.length()> 0){
                // Previne a exibição da verificação para a própria senha do usuário logado.
                if (!login.equalsIgnoreCase(userlogin)){

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

    private void setVerifyLoginVolleyAdapter(){
        String url_login = ServerInfo.SERVER_URL + "/" + ServerInfo.LOGIN_URL;
        mVlAdapt.setFunction("remember");
        mVlAdapt.setURL(url_login);
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
                    if (msgResp.contains(".php nao foi encontrado")) {
                        msgError(msgResp);
                    } else {
                        if (flag.equalsIgnoreCase("remember") && msgResp.indexOf("Erro") == -1) {
                            JSONObject resp = new JSONObject(jResp.getString("data"));
                            if (resp.has("email")) {
                                String login =  mInputLogin.getText().toString().trim();
                                String msgErro = String.format(
                                        "O login '%s' já foi cadastrado no sistema. Tente Outro!", login);
                                msgError(msgErro);
                                mCheckLoginExiste = true;
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

    @Override
    public void notifyListener(VolleyError erro, String flag) {
        Log.i("Script", "Erro: " + erro);
        dismissLoadingDialog();
    }

    private Boolean validadeFields(){

        boolean cancel = false;
        View focusView = null;

        mInputFullName.setError(null);
        mInputRazaoSocial.setError(null);
        mInputLogin.setError(null);
        mInputEmail.setError(null);
        mInputWhatsApp.setError(null);
        mInputTelefone.setError(null);

        String nome = mInputFullName.getText().toString().trim();
        String razao = mInputRazaoSocial.getText().toString().trim();
        String login = mInputLogin.getText().toString().trim();
        String email = mInputEmail.getText().toString().trim();
        String whats = mInputWhatsApp.getText().toString().trim();
        String tele = mInputTelefone.getText().toString().trim();

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
                    "O login '%s' já foi cadastrado no sistema. Tente Outro!", login);
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
        else if(tele.replace("-","").length()<11){
            cancel = true;
            focusView = mInputTelefone;
            mInputTelefone.setError("Nr. whatsapp inválido");
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
