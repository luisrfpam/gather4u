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
import org.ufam.gather4u.activities.fragments.UserColetorCadDadosFragment;
import org.ufam.gather4u.activities.fragments.UserColetorCadEnderecoFragment;
import org.ufam.gather4u.activities.fragments.UserColetorCadRegioesFragment;
import org.ufam.gather4u.activities.fragments.UserColetorCadResiduoFragment;
import org.ufam.gather4u.activities.fragments.adapter.CustomFragmentPagerAdapter;

public class UserColetorCadActivity extends RequestPermitionsActivity
        implements UserColetorCadDadosFragment.OnFragmentInteractionListener,
                   UserColetorCadEnderecoFragment.OnFragmentInteractionListener,
                   UserColetorCadResiduoFragment.OnFragmentInteractionListener,
                   UserColetorCadRegioesFragment.OnFragmentInteractionListener,
                   ViewPager.OnPageChangeListener {

    private static final String TAG = UserColetorCadActivity.class.getSimpleName();

    private FragmentManager mFM = null;
    private ViewPager mPager = null;
    private CustomFragmentPagerAdapter mAdapter = null;
    int mFragAnterior = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_coletor);

        /** Getting a reference to the ViewPager defined the layout file */
        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.addOnPageChangeListener( this );

        /** Getting fragment manager */
        mFM = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
        mAdapter = new CustomFragmentPagerAdapter(mFM);



        // Add Fragments to adapter one by one
        mAdapter.addFragment(getString(R.string.title_cad_coletor_dados),
                UserColetorCadDadosFragment.newInstance());

        mAdapter.addFragment(getString(R.string.title_cad_coletor_ender),
                UserColetorCadEnderecoFragment.newInstance());

        mAdapter.addFragment(getString(R.string.title_cad_coletor_residuos),
                UserColetorCadResiduoFragment.newInstance());

        mAdapter.addFragment(getString(R.string.title_cad_coletor_regioes),
                UserColetorCadRegioesFragment.newInstance());

        mPager.setAdapter(mAdapter);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager( mPager );

        hideKeyboard();
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
        hideKeyboard();
    }

    public void movePreviousFragment () {
        mPager.setCurrentItem(mPager.getCurrentItem()-1, true);
        hideKeyboard();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {

        int currItem = mPager.getCurrentItem();
        Fragment fragment = mFM.getFragments().get(currItem);

        if (currItem == 0){
            ((UserColetorCadDadosFragment)fragment).onActivityResult(requestCode,  resultCode, result);
        }
        else if (currItem == 1){
            ((UserColetorCadEnderecoFragment)fragment).onActivityResult(requestCode, resultCode, result);
        }
        else if (currItem == 2){
            ((UserColetorCadResiduoFragment)fragment).onActivityResult(requestCode, resultCode, result);
        }

        else if (currItem == 3){
            ((UserColetorCadRegioesFragment)fragment).onActivityResult(requestCode, resultCode, result);
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
            Fragment frag = mAdapter.getFragment(mFragAnterior);
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
