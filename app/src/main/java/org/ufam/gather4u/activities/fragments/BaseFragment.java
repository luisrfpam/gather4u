package org.ufam.gather4u.activities.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.LoginActivity;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.activities.main.MainActivity;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.eventbus.CustomEventMessage;
import org.ufam.gather4u.services.JsonVolleyAdapter;

import java.io.ByteArrayOutputStream;

public class BaseFragment extends Fragment
        implements View.OnClickListener {

    public final String TAG = this.getClass().getSimpleName();

    private Dialog mMsgDialog = null;
    public Constants.MsgResult mDlgResult = Constants.MsgResult.OK;
    private ProgressDialog progress;
    private int[] mDlgImgs = null;
    private static final int DLG_DISPLAY_TIME = 3000;
    private static final int RESULT_OK = -1;

    protected OnFragmentInteractionListener mListener;
    protected Activity mActParent = null;
    protected JsonVolleyAdapter mVlAdapt = null;

    protected Button btnOK =  null;
    protected Button btnSim = null;
    protected Button btnNao = null;

    public static LocationManager mManager = null;
    public static ConnectivityManager mConnectivity = null;

    /**
     * Constructor
     */
    public BaseFragment(){
        mActParent = getActivity();
        mVlAdapt = new JsonVolleyAdapter();
        mDlgImgs = new int[] { R.drawable.ok, R.drawable.info, R.drawable.error,
                R.drawable.exclamation, R.drawable.question };

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // Called in the same thread (default)
    // ThreadMode is optional here
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessage(CustomEventMessage event) {
        Log.i("EventBus", "onMessage: " + event.getClassReference());
    }

    // UI updates must run on MainThread
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(CustomEventMessage event) {
        Log.i("EventBus", "onEvent: " + event.getClassReference());
//        MessageEvent stickyEvent = EventBus.getDefault().removeStickyEvent(event.getClass());
//        if(stickyEvent != null) {
//            // Now do something with it
//        }
    }

    public void EventPost(Object message){
        EventBus.getDefault().post( message );
    }

    // To be implemented ...
    public void doSwipeCheck() { }

    @Override
    public void onClick(View view) {  }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            if (mActParent==null){
                mActParent = (RequestPermitionsActivity)context;
            }
            if (mVlAdapt != null){
                mVlAdapt.Start();
            }
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mVlAdapt != null){
            mVlAdapt.Stop();
        }
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkAvailable()) {
            msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
        }
    }

    public boolean isNetworkAvailable() {

        mConnectivity = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

        if (mActParent != null){
            if ( mManager == null){
                mManager = (LocationManager)mActParent.getSystemService(Context.LOCATION_SERVICE );
            }
        }

        return !mManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
    }

    public Boolean isMsgGPSAvailable(){
        Boolean res = isGPSAvailable();
        if (!res){
            msgToastError("GPS is disabled!");
        }
        return res;
    }

    public void hideKeyboard(){
        try{
            mActParent.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideKeyboard(Context context){
        try{
            View view = mActParent.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)mActParent.getSystemService(
                        context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void goToMain(){
        Intent intent = new Intent(mActParent, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void goToLogin(int userType){
        Intent intent = new Intent(mActParent, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("usertype", userType);
        startActivity(intent);
    }

    public void showDlgData(Context context, String title){

        if (mMsgDialog != null ) { mMsgDialog.dismiss(); }

        mMsgDialog = new Dialog(this.getContext(), R.style.FullHeightDialog);
        mMsgDialog.setContentView(R.layout.dialog_calendar);
        TextView msgTitle = (TextView) mMsgDialog.findViewById(R.id.title);

        btnOK = (Button) mMsgDialog.findViewById(R.id.btn_select);
        //btnOK.setVisibility( View.VISIBLE );

        msgTitle.setText(title);
        setClick(btnOK);

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

    public void showToastMsg(String title, String message, Constants.ImageType type, final Boolean goBack){

        if (mMsgDialog != null ) { mMsgDialog.dismiss(); }
            mMsgDialog = new Dialog(mActParent, R.style.FullHeightDialog);
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
                            mMsgDialog.dismiss();
                            if (goBack) {
                                getActivity().onBackPressed();
                            }
                        }
                    }
                    catch (Exception ex) {

                    }
                }
            }, DLG_DISPLAY_TIME);

    }

    public void showToastMsg(String title, String message, Constants.ImageType type){

        if (mMsgDialog != null ) { mMsgDialog.dismiss(); }
        mMsgDialog = new Dialog(mActParent, R.style.FullHeightDialog);
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
                if ( mMsgDialog != null){
                    mMsgDialog.dismiss();
                }
            }
        }, DLG_DISPLAY_TIME);
    }

    public void showMsg(String title, String message, Constants.ImageType type){

        if (mMsgDialog != null ) {  mMsgDialog.dismiss(); }
        mMsgDialog = new Dialog(mActParent, R.style.FullHeightDialog);
        mMsgDialog.setContentView(R.layout.dialog_generic_message);
        TextView msgTitle = (TextView) mMsgDialog.findViewById(R.id.title);
        TextView msg = (TextView) mMsgDialog.findViewById(R.id.message);
        ImageView img = (ImageView) mMsgDialog.findViewById(R.id.dlgImage);
        Button btnOK = (Button) mMsgDialog.findViewById(R.id.btn_ok);

        btnOK = (Button) mMsgDialog.findViewById(R.id.btn_ok);
        btnOK.setVisibility( View.GONE );

        btnSim = (Button) mMsgDialog.findViewById(R.id.btn_yes);
        btnSim.setVisibility( View.GONE );

        btnNao = (Button) mMsgDialog.findViewById(R.id.btn_no);
        btnNao.setVisibility( View.GONE );

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

        msgTitle.setText(title);
        msg.setText(message);
        img.setImageResource(mDlgImgs[type.getValor()]);

        if(btnOK!=null){
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.btn_ok:
                            mMsgDialog.dismiss();
                            break;
                    }
                }
            });
        }
        mMsgDialog.show();
    }

    public void msgError(String message){ showMsg("Erro", message, Constants.ImageType.ERROR); }

    public void msgOK(String message){ showMsg("Sucesso", message, Constants.ImageType.OK); }

    public void msgInfo(String message){ showMsg("Informação", message, Constants.ImageType.INFO); }

    public void msgWarning(String message){ showMsg("Atenção", message, Constants.ImageType.EXCLAMATION); }

    public void msgQuestion(String message){ showMsg("Responda", message, Constants.ImageType.QUESTION); }

    public void msgToastOk(String message){ showToastMsg( "Sucesso", message, Constants.ImageType.OK); }

    public void msgToastOk(String message, Boolean goBack){ showToastMsg( "Sucesso", message, Constants.ImageType.OK, goBack); }

    public void msgToastInfo(String message){ showToastMsg( "Informação", message, Constants.ImageType.INFO); }

    public void msgToastError(String message){ showToastMsg( "Erro", message, Constants.ImageType.ERROR); }

    public void msgToastError(String message, Boolean goBack){ showToastMsg( "Erro", message, Constants.ImageType.ERROR, goBack); }

    public void processDlgReturn() { }

    // ############################################################################
    // ######################### Loadind Dialog ###################################
    // ############################################################################
    public void showLoadingDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this.getContext());
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

    protected Boolean IsMarshMelloOrBefore() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.M;
    }

    protected Boolean IsOverMarshMello()
    {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage( inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Boolean validateFields() {
        return true;
    }

    public void saveData() { }
}
