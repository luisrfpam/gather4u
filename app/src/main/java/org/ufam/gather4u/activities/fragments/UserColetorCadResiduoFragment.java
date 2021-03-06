package org.ufam.gather4u.activities.fragments;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.UserColetorCadActivity;
import org.ufam.gather4u.activities.fragments.adapter.ResiduoAdapter;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.eventbus.ColetorEventMessage;
import org.ufam.gather4u.eventbus.CustomEventMessage;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserColetorCadResiduoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserColetorCadResiduoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserColetorCadResiduoFragment extends BaseFragment
        implements View.OnClickListener {

    private Gather_User mGatherUser = null;
    private ResiduoAdapter mResiduoAdapter = null;
    private RecyclerView mRecyclerViewStores = null;

    public UserColetorCadResiduoFragment() {
        // Required empty public constructor
    }

    public static UserColetorCadResiduoFragment newInstance() {
        return new UserColetorCadResiduoFragment();
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
        View v = inflater.inflate(R.layout.fragment_coletor_residuo, container, false);

        mRecyclerViewStores = (RecyclerView) v.findViewById(R.id.residuo_list);
        mResiduoAdapter = new ResiduoAdapter(mActParent);
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
                JSONObject regiao = residuos.getJSONObject(i);
                r.setId( regiao.getInt("id"));
                r.setNome( regiao.getString("residuo"));
                r.setDescricao( regiao.getString("descricao"));
                items.add(r);
            }

            Log.e(TAG,"size: "+items.size());
            mResiduoAdapter.setList(items);

        }
        catch (Exception ex){
        }

        Button btnNext = (Button) v.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        return v;
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
            case R.id.btn_next:

                if (validateFields()){
                    saveData();
                    ((UserColetorCadActivity)mActParent).moveNextFragment();
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
    private List<Integer> getIdsCheckResiduos(){

        List<Integer> result = new ArrayList<Integer>();
        List<Residuo> listItems = mResiduoAdapter.getListItem();
        for (int i = 0; i < listItems.size(); i++ ) {
            if (listItems.get(i).getChecked() == Boolean.TRUE) {
                result.add(listItems.get(i).getId());
            }
        }
        return result;
    }

    @Override
    public Boolean validateFields(){

        boolean cancel = false;
        List<Integer> chks = getIdsCheckResiduos();
        cancel = chks.size() == 0;
        if(cancel) {
            msgError("Nenhum resíduo foi selecionado.");
            mRecyclerViewStores.requestFocus();
        }
        return !cancel;
    }

    @Override
    public void onMessage(CustomEventMessage event) {
        if (!event.getClassReference().equalsIgnoreCase(this.TAG)) {
           if (event instanceof ColetorEventMessage) {
                mGatherUser = ((ColetorEventMessage) event).getUser();
            }
            Log.i("EventBus", "onMessage: " + event.getClassReference());
        }
    }

    private void SendDataToRegioesFragment() {

        List<Integer> resids = getIdsCheckResiduos();
        EventPost( new ColetorEventMessage(mGatherUser, resids, null, this.TAG) );
    }


    @Override
    public void saveData() {
        SendDataToRegioesFragment();
    }
}
