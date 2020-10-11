package org.ufam.gather4u.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.Permitions.RequestPermitionsActivity;
import org.ufam.gather4u.activities.fragments.BaseFragment;
import org.ufam.gather4u.activities.fragments.NovaEntregaDisponibilidadeFragment;
import org.ufam.gather4u.activities.fragments.NovaEntregaEnderecoFragment;
import org.ufam.gather4u.activities.fragments.NovaEntregaMapaFragment;
import org.ufam.gather4u.activities.fragments.NovaEntregaResiduosFragment;
import org.ufam.gather4u.activities.fragments.adapter.CustomFragmentPagerAdapter;

public class NovaEntregaActivity
        extends RequestPermitionsActivity
        implements BaseFragment.OnFragmentInteractionListener,
        ViewPager.OnPageChangeListener {

    private static final String TAG = NovaEntregaActivity.class.getSimpleName();
    private FragmentManager mFM = null;
    private ViewPager mPager = null;
    private CustomFragmentPagerAdapter mPagerAdapter = null;
    int mFragAnterior = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_entrega);

        /** Getting a reference to the ViewPager defined the layout file */
        mPager = (ViewPager) findViewById(R.id.pager);

        /** Getting fragment manager */
        mFM = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
        mPagerAdapter = new CustomFragmentPagerAdapter(mFM);

        // Add Fragments to adapter one by one

        mPagerAdapter.addFragment(getString(R.string.title_activity_nova_entrega), NovaEntregaResiduosFragment.newInstance());

        mPagerAdapter.addFragment(getString(R.string.title_cad_coletor_ender), NovaEntregaEnderecoFragment.newInstance());

        mPagerAdapter.addFragment(getString(R.string.title_cad_coletor_local), NovaEntregaMapaFragment.newInstance());

        mPagerAdapter.addFragment(getString(R.string.title_cad_entrega_disponibilidade), NovaEntregaDisponibilidadeFragment.newInstance());

        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener( this );

        hideKeyboard();
    }

    @Override
    public void onResume() {
        if (!isNetworkAvailable()) {
            msgToastError( "Sua internet está desativada. Por favor, ative o acesso a internet!");
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                if(mPager.getCurrentItem() > 0){
                    movePreviousFragment();
                    return true;
                }
                else
                    onBackPressed();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void moveNextFragment () {
        mPager.setCurrentItem(mPager.getCurrentItem()+1, true);
    }

    public void movePreviousFragment () {
        mPager.setCurrentItem(mPager.getCurrentItem()-1, true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {

        int count = mFM.getFragments().size();
        Fragment fragment = mFM.getFragments().get(mPager.getCurrentItem());

        if (mPager.getCurrentItem() == 0){
            fragment.onActivityResult(requestCode,  resultCode, result);
        }

        super.onActivityResult(requestCode, resultCode, result);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mFragAnterior = position;
    }

    @Override
    public void onPageSelected(int position) {
        if (mFragAnterior < position ){
            Fragment frag = mPagerAdapter.getFragment(mFragAnterior);
            if (frag != null) {
                Boolean res = ((BaseFragment)frag).validateFields();
                if (res) {
                    ((BaseFragment)frag).saveData();
                }
                else {
                    onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
                    mPager.setCurrentItem(mFragAnterior, true);
                    hideKeyboard();
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
