package org.ufam.gather4u.activities.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.BaseActivity;
import org.ufam.gather4u.activities.fragments.BaseFragment;
import org.ufam.gather4u.activities.fragments.MainSwipeFragment;
import org.ufam.gather4u.activities.fragments.adapter.CustomFragmentPagerAdapter;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.models.Entrega;
import org.ufam.gather4u.models.Gather_User;
import org.ufam.gather4u.utils.BitmapHandle;
import org.ufam.gather4u.utils.Conv;
import org.ufam.gather4u.utils.DateHandle;
import org.ufam.gather4u.utils.GatherTables;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements View.OnClickListener,
        CustomHttpResponse,
        NavigationView.OnNavigationItemSelectedListener,
        MainSwipeFragment.OnFragmentInteractionListener {

    private FragmentManager mFM = null;
    private ViewPager mPager = null;
    private DrawerLayout mDrawer = null;
    private Gather_User mUser = null;
    private FloatingActionButton newAction = null;
    private GatherTables getTables = null;
    private RatingBar mStars = null;
    private TextView mUserCategory = null;
    private TextView mUserPoints = null;
    private CustomFragmentPagerAdapter mPagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        General.setCurrActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUser = Gather_User.fromJSONObject(General.getLoggedUser());

        getTables = new GatherTables();
        getTables.setListener(this);

        //getUserData();

        if (mDrawer == null){
            mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        newAction = (FloatingActionButton)findViewById(R.id.nova_entrega);
        newAction.setOnClickListener(this);
    }

    private void fillUser(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();

        MenuItem residuo = menu.findItem(R.id.nav_updresiduos);
        MenuItem regiao = menu.findItem(R.id.nav_updregioes);

        TextView name = (TextView)header.findViewById(R.id.userName);
//        TextView email = (TextView)header.findViewById(R.id.userEmail);
        TextView type = (TextView)header.findViewById(R.id.userType);
        ImageView avatar = (ImageView) header.findViewById(R.id.imgAvatar);
        LinearLayout pontos = (LinearLayout) header.findViewById(R.id.pontos);
        LinearLayout categorias = (LinearLayout) header.findViewById(R.id.categorias);
        LinearLayout rate = (LinearLayout) header.findViewById(R.id.rating);

        mStars = (RatingBar) header.findViewById(R.id.avaliacao);
        Drawable progress = mStars.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.WHITE);
        mStars.setActivated(false);

        mUserCategory = (TextView)header.findViewById(R.id.userCategory);
        mUserPoints = (TextView)header.findViewById(R.id.userPoints);
        try {

            actBar = this.getSupportActionBar();
            if (actBar!= null){
                actBar.setTitle(mUser.getNome());

                switch (General.getUserType()){
                    case 2:
                        //actBar.setLogo( getResources().getDrawable(R.drawable.entrega_32x32));
                        type.setText(getText(R.string.button_user_participante));
                        actBar.setSubtitle( Html.fromHtml("<font color='#FFBF00'>" + getText(R.string.button_user_participante) + "</font>") );
                        rate.setVisibility( View.GONE );
                        residuo.setVisible( false );
                        regiao.setVisible( false );

                        mUserPoints.setText( "0" );
                        mUserCategory.setText( "Green" );
                        break;

                    case 3:
                        //actBar.setLogo( getResources().getDrawable(R.drawable.coletor32x32));
                        type.setText(getText(R.string.button_user_coletor));
                        actBar.setSubtitle( Html.fromHtml("<font color='#FFBF00'>" + getText(R.string.button_user_coletor) + "</font>") );
                        pontos.setVisibility( View.GONE );
                        categorias.setVisibility( View.GONE );
                        newAction.setVisibility( View.GONE );
                        mStars.setRating(0);
                        break;
                }
            }

            General.GenerateToken();
            name.setText(mUser.getNome());
            //email.setText(mUser.getEmail());


            String strFoto = mUser.getAvatar();
            if (strFoto != null) {
                if (strFoto.trim().length() > 0){
                    BitmapHandle imageHandler = new BitmapHandle();
                    imageHandler.loadImage(this, avatar, mUser.getLogin(), strFoto);
                }
            }
        }
        catch (Exception ex){
        }
    }

    private void getUserData(){
        switch (General.getUserType()) {
            case 2:
                getTables.getUserPontos(mUser.getId());
                getTables.getEntregas( mUser.getId(), -1, Constants.HttpMessageType.ENTREGASNOVAS );
                getTables.getEntregas( mUser.getId(),  -1, Constants.HttpMessageType.ENTREGASACEITAS );
                getTables.getEntregas( mUser.getId(),  -1, Constants.HttpMessageType.ENTREGASFINALIZADAS );
                break;

            case 3:
                getTables.getUserAvaliacao(mUser.getId());
                getTables.getEntregas(-1, mUser.getId(), Constants.HttpMessageType.ENTREGASNOVAS );
                getTables.getEntregas(-1, mUser.getId(), Constants.HttpMessageType.ENTREGASACEITAS );
                getTables.getEntregas(-1, mUser.getId(), Constants.HttpMessageType.ENTREGASFINALIZADAS );
                break;
        }
    }

    private void initializeViewPages(){

        /** Getting a reference to the ViewPager defined the layout file */
        mPager = (ViewPager) findViewById(R.id.pager);

        /** Getting fragment manager */
        mFM = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
        mPagerAdapter = new CustomFragmentPagerAdapter(mFM);

        mPagerAdapter.addFragment("Entregas Pendentes", MainSwipeFragment.newInstance(Constants.FragmentType.MainSwapPendentes));
        mPagerAdapter.addFragment("Entregas Aceitas", MainSwipeFragment.newInstance(Constants.FragmentType.MainSwapAceitas));
        mPagerAdapter.addFragment("Entregas Finalizadas", MainSwipeFragment.newInstance(Constants.FragmentType.MainSwapRealizadas));


        /** Setting the pagerAdapter to the pager object */
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeViewPages();
        fillUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mPagerAdapter.clearFrames();
    }

    private void ChangeVisibleFragment(BaseFragment frag, LinearLayout layout){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        if (frag.isHidden()) {
            ft.show(frag);
            if (layout != null)
                layout.setVisibility(View.VISIBLE);
        } else {
            ft.hide(frag);
            if (layout != null)
                layout.setVisibility(View.GONE);
        }
        ft.commit();
        // TODO Auto-generated method stub
    }

    @Override
    public void onBackPressed() {

        if (mDrawer != null){

            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                //super.onBackPressed();
                //closeApp();
                this.moveTaskToBack(true);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_upddadosbasicos:
                goEditDados();
                break;

            case R.id.nav_updendereco:
                goEditEndereco();
                break;

            case R.id.nav_updloc:
                goToLocation();
                break;

            case R.id.nav_updpassword:
                goToUpdPassword();
                break;

            case R.id.nav_updresiduos:
                goToUpdResiduos();
                break;

            case R.id.nav_updregioes:
                goToUpdRegioes();
                break;

            case R.id.nav_exit:
                goToUserType();
                finish();
                break;
        }

        if (mDrawer != null){
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {

        int count = mFM.getFragments().size();
        Fragment fragment = mFM.getFragments().get(mPager.getCurrentItem());

        fragment.onActivityResult(requestCode,  resultCode, result);

        super.onActivityResult(requestCode, resultCode, result);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        if (v != null){
            int id = v.getId();
            switch (id){
                case R.id.nova_entrega:
                    goToNovaEntrega();
                break;
            }
        }
    }

    @Override
    public void OnHttpResponse(JSONObject response, Constants.HttpMessageType flag) {

        try {
            switch (flag) {

                case PONTOS:
                    if (General.getPontos() != null){
                        JSONObject joPontos = General.getPontos().getJSONObject(0);
                        mUserPoints.setText( joPontos.getString("pontos").toString() );
                        mUserCategory.setText( joPontos.getString("categ").toString() );
                    }
                    break;

                case AVALIACAO:
                    if (General.getAvaliacao() != null) {
                        JSONObject joAval = General.getPontos().getJSONObject(0);
                        mStars.setRating(Float.parseFloat(joAval.getString("coef").toString()));
                    }
                    break;

                case ENTREGASNOVAS:
                    mPagerAdapter.getFragment(Constants.FragmentType.MainSwapPendentes).
                            mEntregaAdapter.setList(fillDados(response));
                    break;
                case ENTREGASACEITAS:
                    mPagerAdapter.getFragment(Constants.FragmentType.MainSwapAceitas).
                            mEntregaAdapter.setList(fillDados(response));
                    break;

                case ENTREGASFINALIZADAS:mPagerAdapter.getFragment( Constants.FragmentType.MainSwapRealizadas).
                        mEntregaAdapter.setList(fillDados(response));
                    break;
            }
        }
        catch (Exception ex){

        }
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
}
