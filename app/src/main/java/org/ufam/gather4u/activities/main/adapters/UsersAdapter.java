package org.ufam.gather4u.activities.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.models.User;
import org.ufam.gather4u.utils.DateHandle;
import org.ufam.gather4u.R;

import java.io.File;
import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<User> mList;
    private ClickListener mClickListener;

    public UsersAdapter(Context c) {
        mList = new ArrayList<>();
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_user_list, parent, false);
        return new MyViewHolder(v);
    }

    public ArrayList<User> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setList(ArrayList<User> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(UsersAdapter.MyViewHolder holder, final int position) {

        holder.tvRegistration.setText(mList.get(position).getLogin());
        holder.tvFullName.setText(mList.get(position).getNome());
        holder.tvCreatedAt.setText(DateHandle.prettyDate(mList.get(position).getDTCad()));

        String path = mList.get(position).getAvatar();

        if(path!=null){
            final File myImageFile = new File(path);
            Picasso.with(mContext)
                    .load(myImageFile)
                    .into(holder.ivProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tvRegistration;
        private TextView tvFullName;
        private TextView tvCreatedAt;
        private ImageView ivProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvRegistration = (TextView) itemView.findViewById(R.id.tv_registration);
            tvFullName = (TextView) itemView.findViewById(R.id.tv_full_name);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.user_created_at);
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