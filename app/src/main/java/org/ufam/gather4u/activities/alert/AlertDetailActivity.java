package org.ufam.gather4u.activities.alert;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import org.ufam.gather4u.activities.UserDataActivity;
import org.ufam.gather4u.activities.event.EventsListActivity;
import org.ufam.gather4u.models.User;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.BaseActivity;

public class AlertDetailActivity extends BaseActivity implements View.OnClickListener {

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(getString(R.string.subtitle_tootlbar_alert_details));
        }

        if(getIntent().getExtras()!=null){
            mUser = getIntent().getExtras().getParcelable("user");
        }

        findViewById(R.id.btn_account).setOnClickListener(this);
        findViewById(R.id.btn_alerts).setOnClickListener(this);
        findViewById(R.id.btn_events).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
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
        Intent intent;
        switch (v.getId()){
            case R.id.btn_account:
                intent = new Intent(this, UserDataActivity.class);
                intent.putExtra("user",mUser);
                intent.putExtra("is_new_user",false);
                startActivity(intent);
                break;

            case R.id.btn_alerts:
                intent = new Intent(this, AlertsListActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;

            case R.id.btn_events:
                intent = new Intent(this, EventsListActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;

            case R.id.btn_logout:
                performLogout();
                break;
        }
    }
}
