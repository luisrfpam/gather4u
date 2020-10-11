package org.ufam.gather4u.activities.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.UserColetorCadActivity;
import org.ufam.gather4u.activities.fragments.adapter.BairroAdapter;
import org.ufam.gather4u.activities.fragments.adapter.RegiaoAdapter;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.eventbus.ColetorEventMessage;
import org.ufam.gather4u.eventbus.CustomEventMessage;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Bairro;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Regiao;
import org.ufam.gather4u.utils.EmailSender;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserColetorEditRegioesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserColetorEditRegioesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserColetorEditRegioesFragment extends BaseFragment
        implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        CustomVolleyAdapterInterface,
        BaseFragment.OnFragmentInteractionListener{

    private static final int WAITTING_TIME = 600;
    private Spinner mInputRegiao = null;
    private Gather_User mGatherUser = null;
    private RegiaoAdapter mRegiaoAdapter = null;
    private BairroAdapter mBairroAdapter = null;
    //private ListViewBairroAdapter mlistViewAdapter = null;
    private RecyclerView mRecyclerViewStoresRegiao = null;
    private RecyclerView mRecyclerViewStoresBairro = null;
    private List<Integer> mResiduos = null;
    private List<Integer> mRegioes = null;
    private List<Integer> mBairros = null;
    private String mSuccessUpdMsg = "";
    private String mFirstSelectedRegiao = "";
    private int mBairrosIdx = -1;

    private ListView mListView = null;
    // PHP Function from the http Request;
    public static final String KEY_FUNCTION =   "upd_coletor";
    public static final String KEY_TOKEN    =   "token";
    public static final String KEY_USER     =   "user";
    public static final String KEY_RESID    =   "residuos";
    public static final String KEY_REGIOES  =   "regioes";
    public static final String KEY_BAIRROS  =   "bairros";



    private GatherTables getLocData = null;

    public UserColetorEditRegioesFragment() {
        // Required empty public constructor
        getLocData = new GatherTables();
    }

    public static UserColetorEditRegioesFragment newInstance() {
        return new UserColetorEditRegioesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Getting the arguments to the Bundle object */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_coletor_regioes, container, false);

        mRecyclerViewStoresRegiao = (RecyclerView) v.findViewById(R.id.regiao_list);

        mRecyclerViewStoresBairro = (RecyclerView) v.findViewById(R.id.bairros_list);

        //mListView = (ListView) v.findViewById(R.id.bairros_list);

        mInputRegiao = (Spinner) v.findViewById(R.id.input_regioes);
        mInputRegiao.setOnItemSelectedListener(this);

        getLocData.getRegioes(mInputRegiao);

        //LayoutInflater layoutInflater = (LayoutInflater) mActParent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) mActParent.getLayoutInflater();

        // ------------------------------------------------------------------------------------
        mRegiaoAdapter = new RegiaoAdapter(mActParent, layoutInflater);
        LayoutManager layoutManagerStoresRegiao = new LinearLayoutManager(mActParent);

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

        Button btnSave = (Button) v.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        v.post(new Runnable() {
            @Override
            public void run() {
                mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );
                fillUserDataFields();
            }
        });

        return v;
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

    public void onBackPressed() {
        ((UserColetorCadActivity)mActParent).movePreviousFragment();
        mActParent.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                ((UserColetorCadActivity)mActParent).movePreviousFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.btn_save:

                if (!validateFields()){
                    UpdateUser();
                }
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @SuppressLint("NewApi")
    private void newBairroAdapter(){

        mBairroAdapter = new BairroAdapter(mActParent.getLayoutInflater());
        getLocData.setListener(mBairroAdapter);
        LayoutManager layoutManagerStoresBairro = new LinearLayoutManager(mActParent);
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

        mRecyclerViewStoresBairro.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v != null){
                    fillUserBairros();
                }
            }
        });

        // ---------------   LIST_VIEW   --------------------------------------
        //mlistViewAdapter = new ListViewBairroAdapter( mActParent, R.layout.item_bairro_list, null);
        //mListView.setAdapter(mlistViewAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (BaseFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                                EmailSender sender = new EmailSender();
                                sender.sendMessage( mGatherUser.getEmail(),
                                        "Atualização de cadastro - Gather4u",
                                        mSuccessUpdMsg);
                            }

                            General.setLoggedUser( new JSONObject(mGatherUser.toString()) );
                            msgToastOk("Atualização realizada com sucesso", true);
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
    public void onStart() {
        super.onStart();
        mVlAdapt.Start();
    }

    @Override
    public void onStop() {
        if (mVlAdapt != null){
            mVlAdapt.Stop();
        }
        super.onStop();
    }

    @Override
    public void notifyListener(VolleyError erro, String flag) {
        Log.i("notifyListener", "flag: " + flag + " - " + erro.getMessage());
        dismissLoadingDialog();
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
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }

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

    @Override
    public Boolean validateFields(){
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
                jUser.put(KEY_USER, mGatherUser.toString());

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
                for (int id : mResiduos) {
                    ids = new JSONObject();
                    ids.put("idresiduo", id);
                    arr.put(ids);
                }
                jUser.put(KEY_RESID, arr.toString());

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
            Log.i("Save", "coletor: " + e.getMessage());
        }
    }

    private void fillOutrasRegioes()
    {
        int len = mRegiaoAdapter.getItemCount();
        List<Integer> chkLst = getIdsCheckRegioes();
        if (chkLst.size() < len){
            getLocData.getOutrasRegioes(mInputRegiao, chkLst);
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
    public void onMessage(CustomEventMessage event) {
        if (!event.getClassReference().equalsIgnoreCase(this.TAG)) {
           if (event instanceof ColetorEventMessage) {
                mGatherUser = ((ColetorEventMessage) event).getUser();
                mResiduos = ((ColetorEventMessage) event).getResiduos();
                fillUserDataFields();
            }
            Log.i("EventBus", "onMessage: " + event.getClassReference());
        }
    }
}
