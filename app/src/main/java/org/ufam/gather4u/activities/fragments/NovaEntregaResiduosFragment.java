package org.ufam.gather4u.activities.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.NovaEntregaActivity;
import org.ufam.gather4u.activities.fragments.adapter.ResiduoEntregaAdapter;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.eventbus.EntregaEventMessage;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.utils.Conv;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;
import java.util.List;

public class NovaEntregaResiduosFragment extends BaseFragment
        implements View.OnClickListener,
        BaseFragment.OnFragmentInteractionListener {

    private Gather_User mGatherUser = null;
    private ResiduoEntregaAdapter mResiduoAdapter = null;
    private RecyclerView mRecyclerViewStores = null;
    private GatherTables getTables = null;

    public NovaEntregaResiduosFragment() {
        // Required empty public constructor
    }

    public static NovaEntregaResiduosFragment newInstance() {
        NovaEntregaResiduosFragment fragment = new NovaEntregaResiduosFragment();
        return fragment;
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
        View v = inflater.inflate(R.layout.fragment_nova_entrega_residuo, container, false);

        mRecyclerViewStores = (RecyclerView) v.findViewById(R.id.residuo_list);
        mResiduoAdapter = new ResiduoEntregaAdapter(mActParent);
        mResiduoAdapter.setClickListener( new ClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                mResiduoAdapter.clickCheckBox(position);
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
            mRecyclerViewStores.setAdapter(mResiduoAdapter);
        }

        try {
            ArrayList<Residuo> items = new ArrayList<>();
            JSONArray residuos = General.getResiduos();
            for(int i=0;i<residuos.length();i++) {
                Residuo r = new Residuo();
                JSONObject res = residuos.getJSONObject(i);
                r.setId( res.getInt("id"));
                r.setNome( res.getString("residuo"));
                r.setPesoPontuacao(res.getString("peso_pontuacao"));
                items.add(r);
            }

            Log.e(TAG,"size: "+items.size());
            mResiduoAdapter.setList(items);

        }
        catch (Exception ex){
        }

        Button btnNext = (Button) v.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );

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
            case R.id.btn_next:

                if (validateFields()){
                    saveData();
                    ((NovaEntregaActivity)mActParent).moveNextFragment();
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private List<Residuo> getFilledResiduos(){
        List<Residuo> result = new ArrayList<Residuo>();
        List<Residuo> listItems = mResiduoAdapter.getListItem();
        for (int i = 0; i < listItems.size(); i++ ) {
            ResiduoEntregaAdapter.MyViewHolder vh = mResiduoAdapter.getViewHolder(i);
            String peso = vh.tv_peso.getText().toString();
            if (peso != null){
                if (peso.trim().length() > 0) {
                    if (Conv.ToDouble(peso) > 0){
                        listItems.get(i).setPeso(peso);
                        result.add(listItems.get(i));
                    }
                }
            }

        }
        return result;
    }

    @Override
    public Boolean validateFields(){

        boolean cancel = false;
        List<Residuo> chks = getFilledResiduos();
        cancel = chks.size() == 0;
        if(cancel) {
            msgError("Nenhum resíduo foi informado.");
            mRecyclerViewStores.requestFocus();
        }

        return !cancel;
    }

    private void SendDataToRegioesFragment() {
        List<Residuo> resids = getFilledResiduos();
        EventPost( new EntregaEventMessage(resids, null, this.TAG) );
    }

    @Override
    public void saveData() {
        SendDataToRegioesFragment();
    }
}
