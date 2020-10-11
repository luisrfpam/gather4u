package org.ufam.gather4u.activities.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.NovaEntregaActivity;
import org.ufam.gather4u.activities.fragments.adapter.DisponibilidadeAdapter;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.eventbus.CustomEventMessage;
import org.ufam.gather4u.eventbus.EntregaEventMessage;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Disponibilidade;
import org.ufam.gather4u.models.Endereco;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.utils.CustomGson;
import org.ufam.gather4u.utils.DateHandle;
import org.ufam.gather4u.utils.TextMasks;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NovaEntregaDisponibilidadeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NovaEntregaDisponibilidadeFragment extends BaseFragment
        implements View.OnClickListener,
        CustomVolleyAdapterInterface,
        BaseFragment.OnFragmentInteractionListener{

    private EditText mInputHRIni = null;
    private EditText mInputHRFim = null;
    private EditText mInputObs = null;
    private EditText mData = null;
    private Button mBtnSave = null;

    private Button mBtnAdd = null;
    private Button mBtnDel = null;

    private List<Residuo> mResids = null;
    private Endereco mEndereco = null;
    private DisponibilidadeAdapter mDisponibilidadeAdapter = null;
    private RecyclerView mRecyclerViewStores = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION =   "cad_entrega";
    public static final String KEY_TOKEN    =   "token";

    public static final String KEY_RESID    =   "residuos";
    public static final String KEY_PONTO    =   "pontos";
    public static final String KEY_PESO     =   "peso";
    public static final String KEY_DISP     =   "disponibilidades";
    public static final String KEY_ENDER    =   "ender";
    public static final String KEY_OBSERV   =   "obs";

    private Double mPontos = 0.0;

    public NovaEntregaDisponibilidadeFragment() {
        // Required empty public constructor
    }

    public static NovaEntregaDisponibilidadeFragment newInstance() {
        NovaEntregaDisponibilidadeFragment fragment = new NovaEntregaDisponibilidadeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nova_entrega_disponibilidade, container, false);

        mData = (EditText) v.findViewById(R.id.input_dtdisp);
        mData.addTextChangedListener(TextMasks.insertDateGather(mData));

        mInputHRIni = (EditText) v.findViewById(R.id.input_hrini);
        mInputHRIni.addTextChangedListener(TextMasks.insertHoraGather(mInputHRIni));

        mInputHRFim = (EditText) v.findViewById(R.id.input_hrfim);
        mInputHRFim.addTextChangedListener(TextMasks.insertHoraGather(mInputHRFim));

        mInputObs = (EditText) v.findViewById(R.id.input_obs);

        mBtnSave = (Button) v.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);

        mBtnAdd = (Button) v.findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(this);

        mBtnDel = (Button) v.findViewById(R.id.btn_del);
        mBtnDel.setOnClickListener(this);

        mDisponibilidadeAdapter = new DisponibilidadeAdapter(mActParent);
        mRecyclerViewStores = (RecyclerView) v.findViewById(R.id.residuo_list);

        mDisponibilidadeAdapter.setClickListener( new ClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                mDisponibilidadeAdapter.clickCheckBox(position);
            }

            @Override
            public void onLongClickListener(View view, int position) {

            } });

        if(mRecyclerViewStores !=null){
            RecyclerView.LayoutManager mLayoutManagerStores = new LinearLayoutManager(mActParent);
            mRecyclerViewStores.setLayoutManager(mLayoutManagerStores);
            mRecyclerViewStores.setItemAnimator(new DefaultItemAnimator());
            mRecyclerViewStores.setHasFixedSize(true);
            mRecyclerViewStores.setNestedScrollingEnabled(true);
            mRecyclerViewStores.setAdapter(mDisponibilidadeAdapter);
        }

        setSaveDadosVolleyAdapter();

        mData.setText( DateHandle.getDateBR() );
        mData.requestFocus();
        mInputHRIni.requestFocus();
        return v;
    }

    public void onBackPressed() {
        ((NovaEntregaActivity)mActParent).movePreviousFragment();
        mActParent.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                ((NovaEntregaActivity)mActParent).movePreviousFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.btn_save:
                SaveEntrega();
                break;

            case R.id.btn_add:
                AddDisp();
                break;

            case R.id.btn_del:
                RemoveDisp();
                break;
        }
    }

    private void clearFields(){
        mData.setText("");
        mInputHRIni.setText("");
        mInputHRFim.setText("");
    }

    private void AddDisp(){
        try {
            if (newValidate()){
                ArrayList<Disponibilidade> items = new ArrayList<>();
                if (mDisponibilidadeAdapter != null){
                    items = mDisponibilidadeAdapter.getListItem();
                }

                Disponibilidade d = new Disponibilidade();
                d.setData( mData.getText().toString() );
                d.setHrini( mInputHRIni.getText().toString() );
                d.setHrfim( mInputHRFim.getText().toString() );
                items.add(d);
                clearFields();
                mDisponibilidadeAdapter.setList(items);
            }
        }
        catch (Exception ex){
        }
    }

    private void RemoveDisp(){
        try {

            ArrayList<Disponibilidade> items = mDisponibilidadeAdapter.getListItem();
            ArrayList<Disponibilidade> checks = getCheckedDisp();

            if (checks != null) {
                if (checks.size() > 0){
                    items.removeAll(checks);
                    mDisponibilidadeAdapter.setList(items);
                }
                else
                {
                    msgError("Nenhum registro selecionado");
                    mRecyclerViewStores.requestFocus();
                }
            }
            else
            {
                msgError("Nenhum registro selecionado");
                mRecyclerViewStores.requestFocus();
            }
        }
        catch (Exception ex){
        }
    }

    private ArrayList<Disponibilidade> getCheckedDisp(){

        ArrayList<Disponibilidade> result = new ArrayList<>();
        ArrayList<Disponibilidade> listItems = mDisponibilidadeAdapter.getListItem();
        for (int i = 0; i < listItems.size(); i++) {
            Disponibilidade reg = listItems.get(i);
            if (reg.getChecked() == Boolean.TRUE) {
                result.add(reg);
            }
        }
        return result;
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
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void notifyListener(JSONObject response, String flag) {
        Log.i("Script", "Object Resposta: " + response.toString());

        if (response != null){
            try {

                dismissLoadingDialog();

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("erro")){
                     msgError(jResp.getString("erro"));
                }
                else

                if (jResp.has("data")){
                    if (jResp.get("data").toString().contains("100")){
                        // Envia email de boas vindas
                        msgToastOk("Agendamento realizado com sucesso", true);
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

    private void setSaveDadosVolleyAdapter(){
        String url = ServerInfo.SERVER_URL + "/" + ServerInfo.ENTREGAS_URL;
        mVlAdapt.setFunction(KEY_FUNCTION);
        mVlAdapt.setURL(url);
        mVlAdapt.setPOSTMethod();
        mVlAdapt.setListener(this);
    }

    private Boolean newValidate(){
        boolean cancel = false;
        View focusView = null;

        mData.setError(null);
        mInputHRIni.setError(null);
        mInputHRFim.setError(null);
        String data = mData.getText().toString().trim();
        String hrini = mInputHRIni.getText().toString().trim();
        String hrfim = mInputHRFim.getText().toString().trim();

        String dtatual = DateHandle.getDateBR( DateHandle.getDate() );

        if(data.length()<1){
            cancel = true;
            focusView = mData;
            mData.setError("Informe a Data");
        }
        else if(data.replace("/","").length()<8){
            cancel = true;
            focusView = mData;
            mData.setError("Data inválido");
        }
        else if (!DateHandle.checkDate(dtatual, data)){
            cancel = true;
            focusView = mData;
            mData.setError("Data inválida");
        }
        else if(hrini.length()<1){
            cancel = true;
            focusView = mInputHRIni;
            mInputHRIni.setError("Informe a Hora Inicial");
        }
        else if(hrini.length()<5){
            cancel = true;
            focusView = mInputHRIni;
            mInputHRIni.setError("Hora Inicial inválida");
        }
        else if(hrfim.length()<1){
            cancel = true;
            focusView = mInputHRFim;
            mInputHRFim.setError("Informe a Hora Final");
        }
        else if(hrfim.length()<5){
            cancel = true;
            focusView = mInputHRFim;
            mInputHRFim.setError("Hora Final inválida");
        }
        else if (!DateHandle.checkTimings(hrini, hrfim)){
                cancel = true;
                focusView = mInputHRFim;
                mInputHRFim.setError("Hora Final menor que a inicial");
        }
        else if (mEndereco == null){
            cancel = true;
            msgError("Nenhum endereço foi selecionado.");
            ((NovaEntregaActivity)mActParent).movePreviousFragment();
        }
        else if (mResids == null){
            cancel = true;
            msgError("Nenhum resíduo foi informado.");
            ((NovaEntregaActivity)mActParent).movePreviousFragment();
        }

        if(cancel) {
            focusView.requestFocus();
        }
        return !cancel;
    }

    private List<Disponibilidade> getDisponibilidade(){
        List<Disponibilidade> listItems = mDisponibilidadeAdapter.getListItem();
        return listItems;
    }

    @Override
    public void onMessage(CustomEventMessage event) {
        if (!event.getClassReference().equalsIgnoreCase(this.TAG)) {
            if (event instanceof EntregaEventMessage) {
                mResids = ((EntregaEventMessage) event).getResiduos();
                mEndereco = ((EntregaEventMessage) event).getEndereco();
            }
            Log.i("EventBus", "onMessage: " + event.getClassReference());
        }
    }

    private void SaveEntrega() {
        try {

            if (mResids.size() > 0) {

                showLoadingDialog();

                Double peso = 0.0;
                Double pesototal = 0.0;
                Double pontuacao = 0.0;
                mPontos = 0.0;

                for (int i = 0; i < mResids.size(); i++){
                    peso = Double.parseDouble(mResids.get(i).getPeso());
                    pesototal += peso;
                    pontuacao = Double.parseDouble(mResids.get(i).getPesoPontuacao());
                    mPontos += peso * pontuacao;
                }

                Gather_User user = Gather_User.fromJSONObject(General.getLoggedUser());
                String observacao = mInputObs.getText().toString();

                JSONObject jUser = new JSONObject();
                String strToken = General.GenerateToken(user.getLogin());

                jUser.put(KEY_TOKEN, strToken);
                jUser.put("id", user.getId());
                jUser.put(KEY_PONTO, mPontos);
                jUser.put(KEY_OBSERV, observacao);
                jUser.put(KEY_PESO, pesototal);

                JSONArray resArray = new JSONArray();
                JSONObject jObj = null;
                for (int i = 0; i < mResids.size(); i++) {
                    jObj = new JSONObject();
                    Residuo res = mResids.get(i);
                    jObj.put("id", res.getId());
                    jObj.put("residuo", res.getNome());
                    jObj.put("peso", res.getPeso());
                    jObj.put("peso_pontuacao", res.getPesoPontuacao());

                    resArray.put(jObj);
                }
                jUser.put(KEY_RESID, resArray.toString());

                jUser.put(KEY_ENDER, CustomGson.ToJson(mEndereco));

                List<Disponibilidade> disps = getDisponibilidade();
                if (disps.size() > 0) {
                    JSONArray dispArray = new JSONArray();
                    for (int i = 0; i < disps.size(); i++) {
                        jObj = new JSONObject();
                        Disponibilidade disp = disps.get(i);
                        jObj.put("data", disp.getData());
                        jObj.put("hrini", disp.getHrini());
                        jObj.put("hrfim", disp.getHrfim());
                        dispArray.put(jObj);
                    }
                    jUser.put(KEY_DISP, dispArray.toString());
                }

                mVlAdapt.setmParams(jUser);
                mVlAdapt.sendRequest();
            }

        } catch (Exception e) {
        }

    }

}
