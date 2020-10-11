package org.ufam.gather4u.activities.fragments.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import org.json.JSONArray;
import org.ufam.gather4u.activities.fragments.MainSwipeFragment;
import org.ufam.gather4u.application.Constants;

import java.util.ArrayList;
import java.util.List;

public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private int PAGE_COUNT = 3;
    private FragmentManager fm;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private JSONArray dados = null;

    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object){ return PagerAdapter.POSITION_NONE; }

    public void addFragment(String title, Fragment fragment) {
        mFragmentTitleList.add(title);
        mFragmentList.add(fragment);
    }

    public void clearFrames(){
        mFragmentTitleList.clear();
        mFragmentList.clear();
        notifyDataSetChanged();
    }

    /** Returns the number of pages */
    @Override
    public int getCount() {
        return  mFragmentList.size();
    }

    public Fragment getFragment(int i){
        if (i > -1){
            Fragment frag = mFragmentList.get(i);
            if ( frag != null){
                return frag;
            }
        }
        return null;
    }

    public Fragment getFragment(String title){
        if (title.trim().length() > 0){
            int idx = -1;
            for (int i = 0; i < mFragmentTitleList.size(); i++){
                if (mFragmentTitleList.get(i).equalsIgnoreCase(title)){
                    idx = i;
                    break;
                }
            }
            Fragment frag = getFragment(idx);
            if ( frag != null){
                return frag;
            }
        }
        return null;
    }

    public MainSwipeFragment getFragment(Constants.FragmentType type){

        int idx = -1;
        for (int i = 0; i < mFragmentList.size(); i++){
            if (((MainSwipeFragment)mFragmentList.get(i)).mType == type ){
                idx = i;
                break;
            }
        }

        MainSwipeFragment frag = (MainSwipeFragment)getFragment(idx);
        if ( frag != null){
            return frag;
        }

        return null;
    }

    public JSONArray getDados() { return dados; }

    public void setDados(JSONArray dados) { this.dados = dados; }

}

