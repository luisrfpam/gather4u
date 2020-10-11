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
import org.ufam.gather4u.activities.fragments.UserColetorEditDadosFragment;
import org.ufam.gather4u.activities.fragments.UserColetorEditEnderecoFragment;
import org.ufam.gather4u.activities.fragments.UserColetorEditRegioesFragment;
import org.ufam.gather4u.activities.fragments.UserColetorEditResiduoFragment;
import org.ufam.gather4u.activities.fragments.adapter.CustomFragmentPagerAdapter;

public class UserColetorEditActivity extends RequestPermitionsActivity
        implements BaseFragment.OnFragmentInteractionListener{

    private FragmentManager mFM = null;
    private ViewPager mPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_coletor_edit);

        if (actBar != null) {
            actBar = getSupportActionBar();
        }
        actBar.setDisplayHomeAsUpEnabled(true);

        /** Getting a reference to the ViewPager defined the layout file */
        mPager = (ViewPager) findViewById(R.id.pager);

        /** Getting fragment manager */
        mFM = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
        CustomFragmentPagerAdapter pagerAdapter = new CustomFragmentPagerAdapter(mFM);

        // Add Fragments to adapter one by one
        pagerAdapter.addFragment(getString(R.string.title_edit_coletor_dados),
                UserColetorEditDadosFragment.newInstance());

        pagerAdapter.addFragment(getString(R.string.title_edit_coletor_ender),
                UserColetorEditEnderecoFragment.newInstance());

        pagerAdapter.addFragment(getString(R.string.title_edit_coletor_residuos),
                UserColetorEditResiduoFragment.newInstance());
//
        pagerAdapter.addFragment(getString(R.string.title_cad_coletor_regioes),
                UserColetorEditRegioesFragment.newInstance());

        mPager.setAdapter(pagerAdapter);

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

        int count = mFM.getFragments().size();
        Fragment fragment = mFM.getFragments().get(mPager.getCurrentItem());

        if (mPager.getCurrentItem() == 0){
            ((UserColetorEditDadosFragment)fragment).onActivityResult(requestCode,  resultCode, result);
        }
        else if (mPager.getCurrentItem() == 1){
            ((UserColetorEditEnderecoFragment)fragment).onActivityResult(requestCode, resultCode, result);
        }
        else if (mPager.getCurrentItem() == 2){
            ((UserColetorEditResiduoFragment)fragment).onActivityResult(requestCode, resultCode, result);
        }

        else if (mPager.getCurrentItem() == 3){
            ((UserColetorEditRegioesFragment)fragment).onActivityResult(requestCode, resultCode, result);
        }

        super.onActivityResult(requestCode, resultCode, result);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
