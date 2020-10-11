package org.ufam.gather4u.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.ufam.gather4u.R;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int SPLASH_DISPLAY_TIME = 2200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getBooleanExtra("close_app", false)){
            finish();
            return;
        }

//        if(PrefManager.getINSTANCE(this).isLoggedIn()) {
//            goToProfile();
//        }
//        else{
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    goToFirstChoice();
                }
            }, SPLASH_DISPLAY_TIME);
        //}
    }

    private void goToFirstChoice(){
        Intent intent = new Intent(this, FirstChoiceActivity.class);
        startActivity(intent);
    }

//    private void goToLogin(){
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//    }
//
//    private void goToProfile(){
//        User user = PrefManager.getINSTANCE(this).getUserSession();
//        Intent intent = new Intent(this, UserDataActivity.class);
//        intent.putExtra("user",user);
//        intent.putExtra("is_new_user",false);
//        startActivity(intent);
//    }
}
