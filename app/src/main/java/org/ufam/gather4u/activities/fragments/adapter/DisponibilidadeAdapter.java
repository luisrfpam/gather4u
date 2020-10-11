package org.ufam.gather4u.activities.fragments.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ufam.gather4u.R;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.models.Disponibilidade;

import java.util.ArrayList;

public class DisponibilidadeAdapter extends RecyclerView.Adapter<DisponibilidadeAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Disponibilidade> mList = null;
    private ArrayList<MyViewHolder> mViews = null;
    private ClickListener mClickListener = null;
    //private int[] mImgsRegiao = null;
    //private String[] mNomeRegiao = null;

    public DisponibilidadeAdapter(Context c) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public DisponibilidadeAdapter(Context c, LayoutInflater layoutInflater) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mContext = c;
        mLayoutInflater = layoutInflater;
    }

    @Override
    public DisponibilidadeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_entrega_disp_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        mViews.add(vh);
        return vh;
    }

    public ArrayList<Disponibilidade> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setList(ArrayList<Disponibilidade> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public Disponibilidade getItem(int position){
        return mList.get(position);
    }

    public MyViewHolder getViewHolder(int position){
        if (mViews != null){
            return mViews.get(position);
        }
        return null;
    }

    public void clickCheckBox(final int position){
        Disponibilidade r = getItem(position);
        MyViewHolder vh = getViewHolder(position);
        if (vh != null) {
            Boolean chk = !vh.getCheckBoxIsChecked();
            r.setChecked(chk);
            vh.checkBoxClick(chk);
        }
    }

    @Override
    public void onBindViewHolder(DisponibilidadeAdapter.MyViewHolder holder, final int position) {

        int resourceId = R.drawable.ic_information_outline;

        String data = mList.get(position).getData();
        holder.tv_dtcad.setText(data);
        String hrini = mList.get(position).getHrini();
        holder.tv_hrini.setText(hrini);
        String hrfim = mList.get(position).getHrfim();
        holder.tv_hrfim.setText(hrfim);

        Picasso.with(mContext)
                .load(resourceId)
                .error(ContextCompat.getDrawable(mContext,R.drawable.ic_information_outline))
                .into(holder.iv_image);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener,
            CompoundButton.OnCheckedChangeListener {
        private TextView tv_dtcad;
        private TextView tv_hrini;
        private TextView tv_hrfim;
        private ImageView iv_image;
        private CheckBox cb_check;

        public MyViewHolder(View itemView) {
            super(itemView);
            cb_check = (CheckBox) itemView.findViewById(R.id.chk_box);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_dtcad = (TextView) itemView.findViewById(R.id.tv_dtcad);
            tv_hrini = (TextView) itemView.findViewById(R.id.tv_hrini);
            tv_hrfim = (TextView) itemView.findViewById(R.id.tv_hrfim);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            cb_check.setOnCheckedChangeListener(this);
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

        public void checkBoxClick(Boolean value){
            if (cb_check != null){
                cb_check.setChecked(value);
            }
        }

        public Boolean getCheckBoxIsChecked(){
            if (cb_check != null){
                return cb_check.isChecked();
            }
            return false;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView!= null){
                //checkBoxClick(isChecked);
                mList.get(getPosition()).setChecked(isChecked);
            }
        }
    }
}