package org.ufam.gather4u.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.ufam.gather4u.R;
import org.ufam.gather4u.application.Constants;

public class UserTypeChoiceActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mEntregador = null;
    private ImageView mColetor = null;
    private Boolean newUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        mEntregador = (ImageView) findViewById(R.id.participante_button);
        mColetor = (ImageView) findViewById(R.id.coletor_button);

        mEntregador.setOnClickListener(this);
        mColetor.setOnClickListener(this);

        if(getIntent().getExtras()!=null){
            newUser = getIntent().getBooleanExtra("is_new_user",false);
        }

    }

    private void goToNewUserParticipante(){
        Intent intent = new Intent(this, UserEntregadorCadActivity.class);
        intent.putExtra("is_new_user",true);
        startActivity(intent);
    }

    private void goToNewUserColetor(){
        Intent intent = new Intent(this, UserColetorCadActivity.class);
        intent.putExtra("is_new_user",true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.participante_button:
                if (newUser)
                {
                    goToNewUserParticipante();
                }
                else {
                    goToLogin(Constants.USER_PARTICIPANTE);
                }
                break;

            case R.id.coletor_button:
                if (newUser)
                {
                    goToNewUserColetor();
                }
                else {
                    goToLogin(Constants.USER_COLETOR);
                }
                break;

            case R.id.btn_close:
                onBackPressed();
                break;
        }
    }
}
