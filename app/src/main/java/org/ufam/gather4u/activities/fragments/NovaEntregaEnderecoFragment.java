package org.ufam.gather4u.activities.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.NovaEntregaActivity;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.eventbus.CustomEventMessage;
import org.ufam.gather4u.eventbus.EntregaEventMessage;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Endereco;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.utils.GatherTables;
import org.ufam.gather4u.utils.TextMasks;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NovaEntregaEnderecoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NovaEntregaEnderecoFragment extends BaseFragment
        implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        CustomVolleyAdapterInterface,
        AdapterView.OnItemSelectedListener,
        BaseFragment.OnFragmentInteractionListener{

    private EditText mInputCEP = null;
    private EditText mInputLogra = null;
    private EditText mInputNr = null;
    private EditText mInputCompl = null;
    private Spinner mInputEstado = null;
    private Spinner mInputCidade = null;
    private Spinner mInputBairro = null;
    private Spinner mInputTipoResid = null;
    private Button mBtnNext = null;
    private Boolean mInvalidCEP = false;

    private CheckBox mChkCad = null;

    private GatherTables getLocData = null;

    private List<Residuo> mResids = null;
    private Endereco mEndereco = null;
    private Gather_User mGatherUser = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION =   "cad_entrega";
    public static final String KEY_TOKEN    =   "token";
    public static final String KEY_USER     =   "user";

    public NovaEntregaEnderecoFragment() {
        // Required empty public constructor
        getLocData = new GatherTables();
    }

    public static NovaEntregaEnderecoFragment newInstance() {
        NovaEntregaEnderecoFragment fragment = new NovaEntregaEnderecoFragment();
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
        View v = inflater.inflate(R.layout.fragment_nova_entrega_ender, container, false);


        mChkCad = (CheckBox) v.findViewById(R.id.chk_cadastro);
        mChkCad.setOnCheckedChangeListener(this);

        mInputCEP = (EditText) v.findViewById(R.id.input_cep);
        mInputCEP.addTextChangedListener(TextMasks.insertCep(mInputCEP));

        mInputLogra = (EditText) v.findViewById(R.id.input_logradouro);
        mInputNr = (EditText) v.findViewById(R.id.input_nr);
        mInputCompl = (EditText) v.findViewById(R.id.input_complemento);

        mInputEstado = (Spinner) v.findViewById(R.id.input_estado);
        mInputCidade = (Spinner) v.findViewById(R.id.input_cidade);
        mInputBairro = (Spinner) v.findViewById(R.id.input_bairro);
        mInputTipoResid = (Spinner) v.findViewById(R.id.input_tiporesid);

        mBtnNext = (Button) v.findViewById(R.id.btn_next);

        getLocData.getEstados(mInputEstado);
        getLocData.getCidades(mInputCidade);
        getLocData.getBairros(mInputBairro);
        getLocData.getTipoResid(mInputTipoResid);

        mInputCEP.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    searchCEP();
            }
        });

        mInputEstado.setOnItemSelectedListener(this);
        mInputCidade.setOnItemSelectedListener(this);
        mInputBairro.setOnItemSelectedListener(this);
        mInputTipoResid.setOnItemSelectedListener(this);
        mBtnNext.setOnClickListener(this);

        return v;
    }

    private void fillUserDataFields(){
        try {

            mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );

            if ( mGatherUser != null){
                mInputCEP.setText( mGatherUser.getCep() );
                mInputLogra.setText( mGatherUser.getLogradouro() );
                mInputNr.setText( mGatherUser.getNr() );
                mInputCompl.setText( mGatherUser.getComplemento() );

                int idcidade = mGatherUser.getIdCidade();

                JSONObject cidade = General.getItemByFieldValue(General.getCidades(), "" +
                        "id", String.valueOf(idcidade));

                int idestado = cidade.getInt("idestado");

                mInputEstado.setSelection( idestado - 1 );

                mInputCidade.setSelection( idcidade - 1 );

                //getLocData.setSpinnerItem( mInputCidade, cidadeidx - 1 );

                int regBairroId = mGatherUser.getIdRegBairro();

                if (regBairroId > 0){

                    JSONObject regbairro = General.getItemByFieldValue(General.getRegiaoBairros(), "" +
                            "id", String.valueOf(regBairroId));

                    int idbairro = regbairro.getInt("idbairro");

                    mInputBairro.setSelection( idbairro - 1 );
                }
                searchCEP();
            }
        }
        catch (Exception ex){
        }
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
            case R.id.btn_next:

                if (validateFields()){
                    saveData();
                    ((NovaEntregaActivity)mActParent).moveNextFragment();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView != null){
            if (adapterView == mInputEstado){
                int idxEstado = getLocData.getDefaultValue(getLocData.DEFAULT_ESTADO);
                if (idxEstado > -1) {
                    if (idxEstado != i) {
                        getLocData.clearSpinner(mInputCidade);
                        getLocData.clearSpinner(mInputBairro);
                    } else {
                        getLocData.getCidades(mInputCidade);
                        getLocData.getBairros(mInputBairro);
                    }
                }
            }
            else if (adapterView == mInputCidade){

                int idxCidade = getLocData.getDefaultValue(getLocData.DEFAULT_CIDADE);
                if (idxCidade > -1){

                    if ( idxCidade != i ){
                        getLocData.clearSpinner(mInputBairro);
                    }
                    else if ( mInputBairro.getCount() == 1) {
                        getLocData.getBairros(mInputBairro);
                    }
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

                JSONObject jResp = new JSONObject(response.toString());
                if (jResp.has("cep")) {

                    mInvalidCEP = false;
                    mInputLogra.setText( jResp.getString("logradouro") );

                    String uf = jResp.getString("uf");
                    if (General.getEstados() != null){

                        int idxEstados = General.getEstadoByUF(uf);
                        getLocData.setSpinnerItem(mInputEstado, idxEstados);
                    }
                    getLocData.fillSpinnerCidades(null);
                    String cidade = jResp.getString("localidade");
                    getLocData.setSpinnerItem(mInputCidade, cidade);

                    mInputCidade.invalidate();

                    getLocData.fillSpinnerBairros(null);
                    String bairro = jResp.getString("bairro");
                    getLocData.setSpinnerItem(mInputBairro, bairro);
                    mInputBairro.invalidate();

                    mInputNr.requestFocus();
                }
                else if (jResp.has("erro")){

                    if( flag.equalsIgnoreCase("cep")) {
                        mInvalidCEP = true;
                        msgError(String.format("CEP %s inválido!", mInputCEP.getText()));
                    }
                    else { msgError(jResp.getString("erro")); }
                }
            }
            catch (Exception ex){
                msgError("Erro: " + ex.toString());
            }
        }
    }

    @Override
    public void notifyListener(VolleyError erro, String flag) {
        msgError(erro.getMessage());
    }

    private void searchCEP()
    {
        setCEPVolleyAdapter();
    }

    private void setCEPVolleyAdapter(){

        String cep = mInputCEP.getText().toString().trim();
        cep = cep.replace("-","");
        if (cep.length() > 0){
            mInvalidCEP = false;
            if (cep.length() == 8){
                String url = String.format(ServerInfo.SRC_CEP_URL, cep);
                mVlAdapt.setFunction("cep");
                mVlAdapt.setURL(url);
                mVlAdapt.setGETMethod();
                mVlAdapt.setListener(this);
                mVlAdapt.sendRequest();
            }
            else
            {
                mInvalidCEP = true;
                msgError("CEP inválido");
            }
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
    public Boolean validateFields(){

        boolean cancel = false;
        View focusView = null;

        mInputCEP.setError(null);
        mInputLogra.setError(null);
        mInputNr.setError(null);

        String cep = mInputCEP.getText().toString().trim();
        String logra = mInputLogra.getText().toString().trim();
        String nr = mInputNr.getText().toString().trim();

        if(cep.length()<1){
            cancel = true;
            focusView = mInputCEP;
            mInputCEP.setError("Informe o CEP");
        }
        else if(cep.replace("-","").length()<8){
            cancel = true;
            focusView = mInputCEP;
            mInputCEP.setError("CEP inválido");
        }
        else if(mInvalidCEP){
            cancel = true;
            focusView = mInputCEP;
            mInputCEP.setError("CEP inválido");
        }
        else if(logra.length()<1){
            cancel = true;
            focusView = mInputLogra;
            mInputLogra.setError("Informe o logradouro");
        }
        else if(nr.length()<1){
            cancel = true;
            focusView = mInputNr;
            mInputNr.setError("Informe o nr.");
        }
        else if (mResids == null){
            msgError("Nenhum resíduo foi selecionado.");
            ((NovaEntregaActivity)mActParent).movePreviousFragment();
        }

        if(cancel) {
            focusView.requestFocus();
        }
        return !cancel;
    }

    @Override
    public void onMessage(CustomEventMessage event) {
        if (!event.getClassReference().equalsIgnoreCase(this.TAG)) {
            if (event instanceof EntregaEventMessage) {
                mResids = ((EntregaEventMessage) event).getResiduos();
            }
            Log.i("EventBus", "onMessage: " + event.getClassReference());
        }
    }

    private void SendDataToMapaFragment() {

        try {
            String cep = mInputCEP.getText().toString().trim();
            String logra = mInputLogra.getText().toString().trim();
            String nr = mInputNr.getText().toString().trim();
            String compl = mInputCompl.getText().toString().trim();

            long idEstado = mInputEstado.getSelectedItemPosition() + 1;
            long idCidade = mInputCidade.getSelectedItemPosition() + 1;
            long idBairro = mInputBairro.getSelectedItemPosition() + 1;
            long idTpResid = mInputTipoResid.getSelectedItemPosition() + 1;

            String estado = mInputEstado.getSelectedItem().toString();
            String cidade = mInputCidade.getSelectedItem().toString();
            String bairro = mInputBairro.getSelectedItem().toString();

            mEndereco = new Endereco();

            mEndereco.setCep(cep);
            mEndereco.setLogradouro(logra);
            mEndereco.setNr(nr);
            mEndereco.setComplemento(compl);
            mEndereco.setIdcidade((int)idCidade);
            mEndereco.setIdbairro((int)idBairro);
            mEndereco.setIdTipoResid((int)idTpResid);
            mEndereco.setBairro(bairro);
            mEndereco.setCidade(cidade);
            mEndereco.setEstado(estado);

            EventPost( new EntregaEventMessage(mResids, mEndereco, this.TAG) );

        } catch (Exception e) {
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView != null){
            if (isChecked){
                fillUserDataFields();
            }
        }
    }

    @Override
    public void saveData() {
        SendDataToMapaFragment();
    }
}
