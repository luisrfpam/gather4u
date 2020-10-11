package org.ufam.gather4u.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import org.ufam.gather4u.R;
import org.ufam.gather4u.application.General;
import org.ufam.gather4u.utils.CustomSwipeAdapter;
import org.ufam.gather4u.utils.GatherTables;

import java.util.Timer;
import java.util.TimerTask;

public class FirstChoiceActivity extends BaseActivity implements View.OnClickListener {

    private TextView mMessage;
    private TextView mTitle;
    private Button mEntrar = null;
    private Button mNewUser = null;
    private ViewPager mViewPager = null;
    private CustomSwipeAdapter mAdapt = null;

    private static final int MSG_DISPLAY_TIME = 5000;
    private Timer mTimet = null;

    private String[] mTitles = null;
    private String[] mMessages = null;

    private LinearLayout mDotsPanel = null;
    private ImageView[] mDotImages = null;
    private int dotsCount = 0;

    private Drawable mInactiveImg = null;
    private Drawable mActiveImg = null;

    private GatherTables getLocData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_choice);

        // Carrega os avatares de todos os usuários
        General.setCurrActivity(this);
        General.setAppContext(this.getApplicationContext());

        mEntrar = (Button) findViewById(R.id.login_user_button);
        mNewUser = (Button) findViewById(R.id.new_user_button);
        mMessage = (TextView) findViewById(R.id.splashMsg);
        mTitle = (TextView) findViewById(R.id.splashTitle);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapt = new CustomSwipeAdapter(this);
        mViewPager.setAdapter(mAdapt);

        mDotsPanel = (LinearLayout) findViewById(R.id.sliderDots);
        dotsCount = mAdapt.getCount();
        mDotImages = new ImageView[dotsCount];

        mInactiveImg = ContextCompat.getDrawable( getApplicationContext(), R.drawable.inactive_dot);
        mActiveImg = ContextCompat.getDrawable( getApplicationContext(), R.drawable.active_dot);

        LayoutParams params = new LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        params.setMargins(8,0,8,0);

        for (int i = 0; i < dotsCount; i++ ){
            mDotImages[i] = new ImageView(this);
            mDotImages[i].setImageDrawable(mInactiveImg);
            mDotsPanel.addView(mDotImages[i], params);
        }
        mDotImages[0].setImageDrawable(mActiveImg);

        mMessages = new String[] {
                getResources().getString(R.string.msg_recicle_01),
                getResources().getString(R.string.msg_recicle_02),
                getResources().getString(R.string.msg_recicle_03)};

        mTitles = new String[] {
                getResources().getString(R.string.title_recicle_01),
                getResources().getString(R.string.title_recicle_02),
                getResources().getString(R.string.title_recicle_03)};


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                ChangeDotSelected(position);
                mMessage.setText(mMessages[position]);
                mTitle.setText(mTitles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //mMessage.setEnabled(false);
        //setTitle("Gather4u - Recicle");

        mMessage.setText(mMessages[0]);
        mTitle.setText(mTitles[0]);
//        mIvChange.setImageResource(R.drawable.splash01);
//        mIvChange.setTag(R.drawable.splash01);
//        timerHandler = new Handler();
        mEntrar.setOnClickListener(this);
        mNewUser.setOnClickListener(this);

        getLocData = new GatherTables();

        try {

            if (isNetworkAvailable()){
                getLocData.defaultFillArrays();
            }
            else
            {
               msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
            }
        }
        catch (Exception ex){

        }
    }

    private void startTimer(){
        mTimet = new Timer();
        mTimet.scheduleAtFixedRate(new ImageChangeTask(), MSG_DISPLAY_TIME, MSG_DISPLAY_TIME);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_user_button:
                mTimet.cancel();
                goToUserType();
                break;

            case R.id.new_user_button:
                mTimet.cancel();
                if (isNetworkAvailable()) {
                    goToNewUser();
                }
                else{
                    msgToastError("Sua internet está desativada. Por favor, ative o acesso a internet!");
                }
                break;

            case R.id.btn_close:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimet.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mTimet.cancel();
    }

    @Override
    public void onBackPressed() {
        closeApp();
    }

    private void ChangeDotSelected(int page){
        for (int i = 0; i < dotsCount; i++ ){
            mDotImages[i].setImageDrawable(mInactiveImg);
        }
        mDotImages[page].setImageDrawable(mActiveImg);
    }

    public class ImageChangeTask extends TimerTask {

        @Override
        public void run() {
            FirstChoiceActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    int idx = mViewPager.getCurrentItem();

                    if (idx < 2){
                        idx++;
                    }else {
                        idx = 0;
                    }
                    mViewPager.setCurrentItem(idx);
                    mMessage.setText(mMessages[idx]);
                    mTitle.setText(mTitles[idx]);
                    ChangeDotSelected(idx);
                }
            });
        }
    }

}