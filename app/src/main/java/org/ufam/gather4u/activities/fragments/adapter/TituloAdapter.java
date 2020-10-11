package org.ufam.gather4u.activities.fragments.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ufam.gather4u.R;
import org.ufam.gather4u.interfaces.ClickListener;

import java.util.ArrayList;

public class TituloAdapter extends RecyclerView.Adapter<TituloAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mList;
    private ClickListener mClickListener;


    public TituloAdapter(Context c) {
        mList = new ArrayList<>();
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public TituloAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_regiao_titulo, parent, false);
        return new MyViewHolder(v);
    }

    public ArrayList<String> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setList(ArrayList<String> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public String getItem(int position){
        return mList.get(position);
    }

    @Override
    public void onBindViewHolder(TituloAdapter.MyViewHolder holder, final int position) {

        int resourceId = R.drawable.ic_mapa_hdpi;

        holder.tv_titulo.setText( mList.get(position));

        Picasso.with(mContext)
                .load(resourceId)
                .error(ContextCompat.getDrawable(mContext,R.drawable.ic_information_outline))
                .into(holder.iv_card);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tv_titulo;
        private ImageView iv_card;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_card = (ImageView) itemView.findViewById(R.id.iv_card);
            tv_titulo = (TextView) itemView.findViewById(R.id.tv_title);

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