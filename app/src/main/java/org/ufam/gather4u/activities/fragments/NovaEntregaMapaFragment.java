package org.ufam.gather4u.activities.fragments;

import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.SupportMapFragment;

import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.NovaEntregaActivity;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.eventbus.CustomEventMessage;
import org.ufam.gather4u.eventbus.EntregaEventMessage;
import org.ufam.gather4u.models.Endereco;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.utils.Conv;
import org.ufam.gather4u.utils.GPSLocation;
import org.ufam.gather4u.utils.GatherTables;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NovaEntregaMapaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NovaEntregaMapaFragment extends BaseFragment
        implements View.OnClickListener,
        LocationListener,
        BaseFragment.OnFragmentInteractionListener{

    private Button mBtnNext = null;
    private GatherTables getLocData = null;
    private List<Residuo> mResids = null;
    private Endereco mEndereco = null;
    private Gather_User mGatherUser = null;

    private GPSLocation location = null;
    private SupportMapFragment mMap = null;

    private TextView mInputLat = null;
    private TextView mInputLon = null;

    // PHP Function from the http Request;
    public static final String KEY_FUNCTION =   "cad_entrega";
    public static final String KEY_TOKEN    =   "token";

    public NovaEntregaMapaFragment() {
        // Required empty public constructor
        getLocData = new GatherTables();
    }

    public static NovaEntregaMapaFragment newInstance() {
        NovaEntregaMapaFragment fragment = new NovaEntregaMapaFragment();
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
        View v = inflater.inflate(R.layout.fragment_nova_entrega_mapa, container, false);

        mMap = (SupportMapFragment) this.getChildFragmentManager().findFragmentById( R.id.map );


        mInputLat = (TextView) v.findViewById(R.id.input_latitude);
        mInputLon = (TextView) v.findViewById(R.id.input_longitude);

        mBtnNext = (Button) v.findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);

        if (isGPSAvailable()){
            if (mMap != null){
                //mMap.setMyLocationEnabled(true);
                location = new GPSLocation(this.getContext(), this.getActivity());
                if (location.isGranted()){
                    location.setListener(this);
                    location.setFragment(mMap);
                    location.Start();
                }
                else
                {
                    msgToastError("Ative o GPS para continuar");
                }
            }
        }
        else
        {
            msgToastError("Ative o GPS para continuar");
        }
        return v;
    }

    private void fillUserDataFields(){
        try {

            mGatherUser = Gather_User.fromJSONObject( General.getLoggedUser() );

            if ( mGatherUser != null){

                String strEnd = mEndereco.getLogradouro() + ", " + mEndereco.getBairro() + ", " +
                        mEndereco.getCidade() + ", " + mEndereco.getEstado();
                Address enderecos = location.getAddress(strEnd);

                if (enderecos != null){
                    location.setUseGPS(false);
                    location.setLocation( enderecos.getLatitude(), enderecos.getLongitude());
                }

                location.setLocation(enderecos.getLatitude(), enderecos.getLongitude());
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
    public Boolean validateFields(){

        boolean cancel = false;
        View focusView = null;

        String lat = mInputLat.getText().toString();
        String lon = mInputLon.getText().toString();

        if (lat.trim().length() == 0 ){
            cancel = true;
        }

        if(cancel) {
            msgError("Informe a posição da entrega no mapa");
        }
        return !cancel;
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

    private void SendDataToDisponibilidadeFragment() {

        try {

            if (mEndereco != null) {

                String lat = mInputLat.getText().toString();
                String lon = mInputLon.getText().toString();

                mEndereco.setLatitude( Conv.ToDouble(lat) );
                mEndereco.setLongitude( Conv.ToDouble(lon) );

                EventPost(new EntregaEventMessage(mResids, mEndereco, this.TAG));
            }


        } catch (Exception e) {
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        try {
            mInputLat.setText( String.valueOf(location.getLatitude()) );
            mInputLon.setText( String.valueOf(location.getLongitude()) );
        }
        catch (Exception ex){
        }
    }

    @Override
    public void saveData() {
        SendDataToDisponibilidadeFragment();
    }
}
