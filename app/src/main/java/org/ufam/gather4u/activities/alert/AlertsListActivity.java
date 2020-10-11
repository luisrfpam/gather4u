package org.ufam.gather4u.activities.alert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.ufam.gather4u.activities.UserDataActivity;
import org.ufam.gather4u.activities.alert.adapter.AlertsAdapter;
import org.ufam.gather4u.activities.event.EventsListActivity;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.models.Alert;
import org.ufam.gather4u.models.User;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.BaseActivity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class AlertsListActivity extends BaseActivity implements View.OnClickListener {

    private User mUser;
    private static final String TAG = AlertsListActivity.class.getSimpleName();
    private AlertsAdapter mAlertsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts_list);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(getString(R.string.subtitle_tootlbar_alerts_list));
        }

        if(getIntent().getExtras()!=null){
            mUser = getIntent().getExtras().getParcelable("user");
        }

        findViewById(R.id.btn_account).setOnClickListener(this);
        findViewById(R.id.btn_alerts).setVisibility(View.GONE);
        findViewById(R.id.btn_events).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);

        RecyclerView recyclerViewStores = (RecyclerView) findViewById(R.id.alert_list);
        mAlertsAdapter = new AlertsAdapter(this);
        mAlertsAdapter.setClickListener(new ClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                Alert alert = mAlertsAdapter.getItem(position);
                Intent intent = new Intent(AlertsListActivity.this,AlertDetailActivity.class);
                intent.putExtra("alert",alert);
                intent.putExtra("user",mUser);
                startActivity(intent);
            }

            @Override
            public void onLongClickListener(View view, int position) {

            }
        });
        if(recyclerViewStores!=null){
            RecyclerView.LayoutManager mLayoutManagerStores = new LinearLayoutManager(getApplicationContext());
            recyclerViewStores.setLayoutManager(mLayoutManagerStores);
            recyclerViewStores.setItemAnimator(new DefaultItemAnimator());
            recyclerViewStores.setHasFixedSize(true);
            recyclerViewStores.setNestedScrollingEnabled(false);
            recyclerViewStores.setAdapter(mAlertsAdapter);
        }

        //stub
        ArrayList<Alert> items = new ArrayList<>();

        for(int i=0;i<100;i++){
            Alert a = new Alert();
            a.setName("Alerta "+i+1);

            Random r = new Random();
            int Low = 1;
            int High = 3;
            int randomNum = r.nextInt(High-Low) + Low;

            switch (randomNum){
                case Alert.RISK_ELETRICAL_SHOCK:
                    a.setType(Alert.RISK_ELETRICAL_SHOCK);
                    a.setDescription("Risco iminente de choque elétrico. Verifique as condições da rede antes de se aproximar"+(i+1));
                    break;

                case Alert.COSTUME_ITEM_FAILED:
                    a.setType(Alert.COSTUME_ITEM_FAILED);
                    a.setDescription("Não foi possível detectar um ítem do traje. "+(i+1));
                    break;
            }

            Date utilDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(utilDate);
            cal.set(Calendar.MILLISECOND, 0);
            a.setCreatedAt(new Timestamp(utilDate.getTime()).toString());

            items.add(a);
        }
        Log.e(TAG,"size: "+items.size());
        mAlertsAdapter.setList(items);
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