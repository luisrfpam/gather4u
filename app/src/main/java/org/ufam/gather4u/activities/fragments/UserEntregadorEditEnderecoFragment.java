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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.UserEntregadorCadActivity;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.conn.ServerInfo;
import org.ufam.gather4u.eventbus.CustomEventMessage;
import org.ufam.gather4u.eventbus.ParticipanteEventMessage;
import org.ufam.gather4u.interfaces.CustomVolleyAdapterInterface;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.utils.EmailSender;
import org.ufam.gather4u.utils.GatherTables;
import org.ufam.gather4u.utils.TextMasks;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserEntregadorEditEnderecoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserEntregadorEditEnderecoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserEntregadorEditEnderecoFragment extends BaseFragment
        implements View.OnClickListener,
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
    private Button mBtnSave = null;
    private Boolean mInvalidCEP = false;

    private GatherTables getLocData = null;

    private Gather_User mGatherUser = null;
    private String mSuccessUpdMsg = "";

    public LinearLayout mLayout = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION =   "updparticipante";
    public static final String KEY_TOKEN    =   "token";
    public static final String KEY_USER     =   "user";

    public UserEntregadorEditEnderecoFragment() {
        // Required empty public constructor
        getLocData = new GatherTables();
    }

    public static UserEntregadorEditEnderecoFragment newInstance() {
        UserEntregadorEditEnderecoFragment fragment = new UserEntregadorEditEnderecoFragment();
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
        View v = inflater.inflate(R.layout.fragment_edit_entregador_ender, container, false);

        mLayout = (LinearLayout) v.findViewById(R.id.ll_edit_entregador_ender);

        mInputCEP = (EditText) v.findViewById(R.id.input_cep);
        mInputCEP.addTextChangedListener(TextMasks.insertCep(mInputCEP));

        mInputLogra = (EditText) v.findViewById(R.id.input_logradouro);
        mInputNr = (EditText) v.findViewById(R.id.input_nr);
        mInputCompl = (EditText) v.findViewById(R.id.input_complemento);

        mInputEstado = (Spinner) v.findViewById(R.id.input_estado);
        mInputCidade = (Spinner) v.findViewById(R.id.input_cidade);
        mInputBairro = (Spinner) v.findViewById(R.id.input_bairro);

        mBtnSave = (Button) v.findViewById(R.id.btn_save);

        getLocData.getEstados(mInputEstado);
        getLocData.getCidades(mInputCidade);
        getLocData.getBairros(mInputBairro);

        mInputCEP.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    searchCEP();
            }
        });

        mInputEstado.setOnItemSelectedListener(this);
        mInputCidade.setOnItemSelectedListener(this);
        mInputBairro.setOnItemSelectedListener(this);
        mBtnSave.setOnClickListener(this);

        mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );
        fillUserDataFields();
        searchCEP();

        return v;
    }

    private void fillUserDataFields(){
        try {

            if ( mGatherUser != null){
                mInputCEP.setText( mGatherUser.getCep() );
                mInputLogra.setText( mGatherUser.getLogradouro() );
                mInputNr.setText( mGatherUser.getNr() );
                mInputCompl.setText( mGatherUser.getComplemento() );


//            int estadoId = 4; //user.getEstado();
//            getLocData.setSpinnerItem(mInputEstado, estadoId);

                int idcidade = mGatherUser.getIdCidade();

                JSONObject cidade = General.getItemByFieldValue(General.getCidades(), "" +
                        "id", String.valueOf(idcidade));

                int idestado = cidade.getInt("idestado");

                mInputEstado.setSelection( idestado - 1 );

                mInputCidade.setSelection( idcidade - 1 );

                //getLocData.setSpinnerItem( mInputCidade, cidadeidx - 1 );

                int regBairroId = mGatherUser.getIdRegBairro();

                JSONObject regbairro = General.getItemByFieldValue(General.getRegiaoBairros(), "" +
                        "id", String.valueOf(regBairroId));

                int idbairro = regbairro.getInt("idbairro");


                mInputBairro.setSelection( idbairro - 1 );

            }
        }
        catch (Exception ex){
        }
    }

    public void onBackPressed() {
        ((UserEntregadorCadActivity)mActParent).movePreviousFragment();
        mActParent.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //((UserEntregadorEditBasicosActivity)mActParent).movePreviousFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.btn_save:

                if (validateFields()){
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

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof BaseFragment.OnFragmentInteractionListener) {
//            mListener = (BaseFragment.OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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
                else

                if (jResp.has("data")){
                    if (jResp.get("data").toString().contains("100")){

                        // Envia email de boas vindas
                        if (mGatherUser != null){
                            EmailSender sender = new EmailSender();
                            sender.sendMessage( mGatherUser.getEmail(),
                                    "Atualização de cadastro - Gather4u",
                                    mSuccessUpdMsg);
                        }
                        msgToastOk("Atualização realizada com sucesso", true);

                        General.setLoggedUser( new JSONObject(mGatherUser.toString()) );
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

        if(cancel) {
            focusView.requestFocus();
        }
        return !cancel;
    }

    @Override
    public void onMessage(CustomEventMessage event) {
        if (!event.getClassReference().equalsIgnoreCase(this.TAG)) {
            if (event instanceof ParticipanteEventMessage) {
                mGatherUser = ((ParticipanteEventMessage) event).getUser();
                fillUserDataFields();
            }
            Log.i("EventBus", "onMessage: " + event.getClassReference());
        }
    }

    private void UpdateUser() {

        try {
            String cep = mInputCEP.getText().toString().trim();
            String logra = mInputLogra.getText().toString().trim();
            String nr = mInputNr.getText().toString().trim();
            String compl = mInputCompl.getText().toString().trim();

            long idEstado = mInputEstado.getSelectedItemPosition() + 1;
            long idCidade = mInputCidade.getSelectedItemPosition() + 1;
            long idBairro = mInputBairro.getSelectedItemPosition() + 1;

            if (mGatherUser != null) {

                showLoadingDialog();

                mGatherUser.setCep(cep);
                mGatherUser.setLogradouro(logra);
                mGatherUser.setNr(nr);
                mGatherUser.setComplemento(compl);
                mGatherUser.setIdCidade((int) idCidade);
                mGatherUser.setIdBairro((int) idBairro);

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

            setSaveDadosVolleyAdapter();
            mVlAdapt.setmParams(jUser);
            mVlAdapt.sendRequest();

        } catch (Exception e) {
        }

    }
}
