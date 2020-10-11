package org.ufam.gather4u.activities.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.ColetaDetailActivity;
import org.ufam.gather4u.activities.EntregaDetailActivity;
import org.ufam.gather4u.activities.fragments.adapter.EntregaAdapter;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.models.Entrega;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.utils.Conv;
import org.ufam.gather4u.utils.CustomGson;
import org.ufam.gather4u.utils.DateHandle;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainSwipeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainSwipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainSwipeFragment extends Fragment
    implements ClickListener, CustomHttpResponse{

    public final String TAG = this.getClass().getSimpleName();

    private static Context mContext;
    public Constants.FragmentType mType;
    private OnFragmentInteractionListener mListener;

    public EntregaAdapter mEntregaAdapter = null;

    private RecyclerView mRecyclerViewStores = null;
    private GatherTables getTables = null;

    private Gather_User mUser = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type Parameter 1.
     * @return A new instance of fragment CustomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainSwipeFragment newInstance(Constants.FragmentType type) {
        MainSwipeFragment fragment = new MainSwipeFragment();
        fragment.mType = type;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MainSwipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_swipe, container, false);
        TextView title = (TextView ) v.findViewById(R.id.title);
        ImageView image = (ImageView) v.findViewById(R.id.iv_card);
        TextView help = (TextView ) v.findViewById(R.id.helpText);

        mRecyclerViewStores = (RecyclerView) v.findViewById(R.id.list);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getTables = new GatherTables();
        getTables.setListener(this);

        mEntregaAdapter = new EntregaAdapter(layoutInflater);
        mEntregaAdapter.setClickListener(this);
        mRecyclerViewStores.setAdapter(mEntregaAdapter);

        switch (General.getUserType()) {
            case 2:
                image.setImageResource(R.drawable.ic_entrega_hdpi);
                break;
            case 3:
                image.setImageResource(R.drawable.ic_coleta_mdpi);
                break;
        }

        if(mRecyclerViewStores != null) {
            RecyclerView.LayoutManager mLayoutManagerStores = new LinearLayoutManager(this.getContext());
            mRecyclerViewStores.setLayoutManager(mLayoutManagerStores);
            mRecyclerViewStores.setItemAnimator(new DefaultItemAnimator());
            mRecyclerViewStores.setHasFixedSize(true);
            mRecyclerViewStores.setNestedScrollingEnabled(true);
            mRecyclerViewStores.setAdapter(mEntregaAdapter);
        }

        switch (mType) {

            case MainSwapPendentes:
                image.setImageResource(R.drawable.entrega_nova_hdpi);
                title.setText( getString(R.string.title_main_pendentes ) );
                help.setText( getString(R.string.msg_help_coletas_part_pendentes ) );
                break;

            case MainSwapAceitas:
                    switch (General.getUserType()) {
                    case 2:
                        image.setImageResource(R.drawable.entrega_aceita_hdpi);
                        title.setText( getString(R.string.title_main_aceitas ) );
                        help.setText( getString(R.string.msg_help_coletas_part_aceitas ) );
                        break;
                    case 3:
                        image.setImageResource(R.drawable.coleta_aceita_hdpi);
                        title.setText( getString(R.string.title_main_coletas_aceitas ) );
                        help.setText( getString(R.string.msg_help_coletas_colet_aceitas ) );
                        break;
                    }
                break;

            case MainSwapRealizadas:
                    switch (General.getUserType()) {
                        case 2:
                            image.setImageResource(R.drawable.entrega_finalizado_hdpi);
                            title.setText( getString(R.string.title_main_realizadas ) );
                            help.setText( getString(R.string.msg_help_coletas_part_realizados ) );
                            break;
                        case 3:
                            image.setImageResource(R.drawable.coleta_finalizado_hdpi);
                            title.setText( getString(R.string.title_main_coletas_realizadas ) );
                            help.setText( getString(R.string.msg_help_coletas_colet_realizados ) );
                            break;
                    }
                    break;
        }
        mUser = Gather_User.fromJSONObject(General.getLoggedUser());
        return v;
    }

    private ArrayList<Entrega> fillDados(JSONObject resp) {

        ArrayList<Entrega> items = new ArrayList<>();
        try {
            if (resp != null) {
                if (resp.has("data")) {
                    JSONArray dados = resp.getJSONArray(Constants.POSTS);

                    for (int i = 0; i < dados.length(); i++) {
                        Entrega e = new Entrega();
                        JSONObject joEntrega = dados.getJSONObject(i);

                        e.setId(joEntrega.getInt("id"));
                        e.setIdEntregador(joEntrega.getInt("identregador"));
                        e.setIdUsuario(joEntrega.getInt("idusuario"));
                        e.setIdColeta(joEntrega.getInt("idcoleta"));
                        e.setEntregador(joEntrega.getString("entregador"));
                        e.setLogradouro(joEntrega.getString("logradouro"));
                        e.setDTCadstro(DateHandle.getDateBR(joEntrega.getString("dtcadastro")));
                        e.setPesoTotal(joEntrega.getString("pesototal"));
                        e.setPontos(joEntrega.getString("pontos"));
                        e.setTipoResidencia(joEntrega.getString("tiporesidencia"));
                        e.setBairro(joEntrega.getString("bairro"));
                        e.setObs(joEntrega.getString("observacao"));
                        e.setLatitude(Conv.ToDouble(joEntrega.getString("lat")));
                        e.setLongitude(Conv.ToDouble(joEntrega.getString("lon")));
                        e.setEntAval(Conv.ToDouble(joEntrega.getString("ent_aval")));
                        e.setColAval(Conv.ToDouble(joEntrega.getString("col_aval")));

                        if (! Contains( items, e.getId()) ){
                            items.add(e);
                        }
                    }
                    Log.e(TAG, "size: " + items.size());

                    return items;
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    private Boolean Contains(ArrayList<Entrega> list, int id){
        for (int i = 0; i < list.size(); i++ ){
            Entrega ent = list.get(i);
            if (ent != null){
                if (ent.getId() == id){
                    return true;
                }
            }
        }
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (General.getUserType()) {

                    case 2:

                        switch (mType) {
                            case MainSwapPendentes:
                                getTables.getEntregas( mUser.getId(), -1, Constants.HttpMessageType.ENTREGASNOVAS );
                                break;
                            case MainSwapAceitas:
                                getTables.getEntregas( mUser.getId(),  -1, Constants.HttpMessageType.ENTREGASACEITAS );
                                break;
                            case MainSwapRealizadas:
                                getTables.getEntregas( mUser.getId(),  -1, Constants.HttpMessageType.ENTREGASFINALIZADAS );
                                break;
                        }
                        break;

                    case 3:

                        switch (mType) {
                            case MainSwapPendentes:
                                getTables.getEntregas(-1, mUser.getId(), Constants.HttpMessageType.ENTREGASNOVAS );
                                break;
                            case MainSwapAceitas:
                                getTables.getEntregas(-1, mUser.getId(), Constants.HttpMessageType.ENTREGASACEITAS );
                                break;
                            case MainSwapRealizadas:
                                getTables.getEntregas(-1, mUser.getId(), Constants.HttpMessageType.ENTREGASFINALIZADAS );
                                break;
                        }
                        break;
                }

            }
        }, 800);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClickListener(View view, int position) {

        Intent intent = null;
        String strItem = "";

        switch (mType) {

            case MainSwapPendentes:

                intent = new Intent(this.getContext(), EntregaDetailActivity.class);
                strItem = CustomGson.ToJson( mEntregaAdapter.getItem(position) );
                intent.putExtra("entrega", strItem);
                startActivity(intent);

                break;

            case MainSwapAceitas:

                intent = new Intent(this.getContext(), ColetaDetailActivity.class);
                strItem = CustomGson.ToJson( mEntregaAdapter.getItem(position) );
                intent.putExtra("entrega", strItem);
                intent.putExtra("tipo", mType);
                startActivity(intent);
                break;

            case MainSwapRealizadas:

                intent = new Intent(this.getContext(), ColetaDetailActivity.class);
                strItem = CustomGson.ToJson( mEntregaAdapter.getItem(position) );
                intent.putExtra("entrega", strItem);
                intent.putExtra("tipo", mType);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void OnHttpResponse(JSONObject response, Constants.HttpMessageType flag) {

        try {
            switch (flag) {

                case ENTREGASNOVAS:
                    mEntregaAdapter.setList(fillDados(response));
                    break;

                case ENTREGASACEITAS:
                    mEntregaAdapter.setList(fillDados(response));
                    break;

                case ENTREGASFINALIZADAS:
                    mEntregaAdapter.setList(fillDados(response));
                    break;
            }
        }
        catch (Exception ex){

        }
    }

    @Override
    public void onLongClickListener(View view, int position) {

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
