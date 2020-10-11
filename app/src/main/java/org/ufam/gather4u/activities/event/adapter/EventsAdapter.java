package org.ufam.gather4u.activities.event.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.R;
import org.ufam.gather4u.models.Event;
import org.ufam.gather4u.utils.DateHandle;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Event> mList;
    private ClickListener mClickListener;

    public EventsAdapter(Context c) {
        mList = new ArrayList<>();
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_event_list, parent, false);
        return new MyViewHolder(v);
    }

    public ArrayList<Event> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setList(ArrayList<Event> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public Event getItem(int position){
        return mList.get(position);
    }

    @Override
    public void onBindViewHolder(EventsAdapter.MyViewHolder holder, final int position) {

        holder.tvRDescription.setText(mList.get(position).getDescription());
        holder.tvName.setText(mList.get(position).getName());
        holder.tvCreatedAt.setText(DateHandle.prettyDate(mList.get(position).getCreatedAt()));

        int resourceId = R.drawable.ic_information_outline;

        switch (mList.get(position).getType()){

            case Event.AUDIO_CHANGED:
                resourceId = R.drawable.speaker;
                break;

            case Event.VIDEO_CHANGED:
                resourceId = R.drawable.video_player;
                break;


        }

        Picasso.with(mContext)
                .load(resourceId)
                .error(ContextCompat.getDrawable(mContext,R.drawable.ic_information_outline))
                .into(holder.ivProfile);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tvRDescription;
        private TextView tvName;
        private TextView tvCreatedAt;
        private ImageView ivProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvRDescription = (TextView) itemView.findViewById(R.id.tv_event_description);
            tvName = (TextView) itemView.findViewById(R.id.tv_event_name);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tv_event_created_at);
            ivProfile = (ImageView) itemView.findViewById(R.id.iv_profile);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onClickListener(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                mClickListener.onLongClickListener(v, getPosition());
            }
            return false;
        }
    }
}