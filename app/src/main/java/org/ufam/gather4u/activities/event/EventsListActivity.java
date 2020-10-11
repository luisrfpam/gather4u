package org.ufam.gather4u.activities.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.ufam.gather4u.activities.GoogleApiClientActivity;
import org.ufam.gather4u.activities.UserDataActivity;
import org.ufam.gather4u.activities.event.adapter.EventsAdapter;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.models.Event;
import org.ufam.gather4u.models.User;
import org.ufam.gather4u.R;
import org.ufam.gather4u.activities.BaseActivity;
import org.ufam.gather4u.activities.alert.AlertsListActivity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class EventsListActivity extends BaseActivity implements View.OnClickListener {

    private EventsAdapter mEventsAdapter;
    private final static String TAG = EventsListActivity.class.getSimpleName();
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(getString(R.string.subtitle_tootlbar_events_list));
        }

        if(getIntent().getExtras()!=null){
            mUser = getIntent().getExtras().getParcelable("user");
        }

        RecyclerView recyclerViewStores = (RecyclerView) findViewById(R.id.event_list);
        mEventsAdapter = new EventsAdapter(this);
        mEventsAdapter.setClickListener(new ClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                Event event = mEventsAdapter.getItem(position);
                Intent intent = new Intent(EventsListActivity.this,EventDetailActivity.class);
                intent.putExtra("event",event);
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
            recyclerViewStores.setAdapter(mEventsAdapter);
        }

        findViewById(R.id.btn_account).setOnClickListener(this);
        findViewById(R.id.btn_events).setVisibility(View.GONE);
        findViewById(R.id.btn_alerts).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_location).setOnClickListener(this);

        //stub
        ArrayList<Event> items = new ArrayList<>();

        for(int i=0;i<100;i++){
            Event e = new Event();
            e.setName("Evento "+i+1);


            Random r = new Random();
            int Low = 1;
            int High = 3;
            int randomNum = r.nextInt(High-Low) + Low;

            switch (randomNum){

                case Event.VIDEO_CHANGED:
                    e.setType(Event.VIDEO_CHANGED);
                    e.setDescription("Um novo vídeo foi carregado "+(i+1));
                    break;

                case Event.AUDIO_CHANGED:
                    e.setType(Event.AUDIO_CHANGED);
                    e.setDescription("Um novo áudio foi carregado "+(i+1));
                    break;


            }

            Date utilDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(utilDate);
            cal.set(Calendar.MILLISECOND, 0);
            e.setCreatedAt(new Timestamp(utilDate.getTime()).toString());

            items.add(e);
        }
        Log.e(TAG,"size: "+items.size());
        mEventsAdapter.setList(items);
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

            case R.id.btn_logout:
                performLogout();
                break;

            case R.id.btn_location:
                intent = new Intent(this, GoogleApiClientActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
                break;
        }
    }
}
