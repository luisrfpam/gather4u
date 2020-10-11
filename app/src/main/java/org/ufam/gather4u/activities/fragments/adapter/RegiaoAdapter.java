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
import org.ufam.gather4u.models.Regiao;

import java.util.ArrayList;

public class RegiaoAdapter extends RecyclerView.Adapter<RegiaoAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Regiao> mList = null;
    private ArrayList<MyViewHolder> mViews = null;
    private ClickListener mClickListener = null;
    private CompoundButton.OnCheckedChangeListener mChangeListener = null;

    public RegiaoAdapter(Context c, LayoutInflater layoutInflater) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mContext = c;
        mLayoutInflater = layoutInflater;
    }

    @Override
    public RegiaoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_regiao_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        mViews.add(vh);
        return vh;
    }

    public ArrayList<Regiao> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setChangeListener(CompoundButton.OnCheckedChangeListener changeListener){
        this.mChangeListener = changeListener;
    }

    public void setList(ArrayList<Regiao> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public Regiao getItem(int position){
        return mList.get(position);
    }

    public MyViewHolder getViewHolder(int position){
        if (mViews != null){

            Regiao r = getItem(position);

            for (int i = 0; i < mViews.size(); i++){

                RecyclerView.ViewHolder h = mViews.get(i);

                if (r.getNome().equalsIgnoreCase( ((MyViewHolder)h).tv_nome_regiao.getText().toString()) )
                {
                    return (MyViewHolder)h;
                }
            }
        }
        return null;
    }

    public void clickCheckBox(final int position){
        Regiao r = getItem(position);
        MyViewHolder vh = getViewHolder(position);
        if (vh != null) {
            Boolean chk = !vh.getCheckBoxIsChecked();
            r.setChecked(chk);
            vh.checkBoxClick(chk);
        }
    }

    @Override
    public void onBindViewHolder(RegiaoAdapter.MyViewHolder holder, final int position) {

        int resourceId = R.drawable.ic_mapa_hdpi;

        String regiao = mList.get(position).getNome();

        String descricao = String.format("Região %s da cidade", regiao);

        holder.tv_nome_regiao.setText(regiao);

        holder.tv_descricao.setText(descricao);

        Picasso.with(mContext)
                .load(resourceId)
                .error(ContextCompat.getDrawable(mContext,R.drawable.ic_information_outline))
                .into(holder.iv_regiao);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,
            CompoundButton.OnCheckedChangeListener {
        private TextView tv_descricao;
        private TextView tv_nome_regiao;
        //private TextView tvCreatedAt;
        private ImageView iv_regiao;
        private CheckBox cb_check;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_regiao = (ImageView) itemView.findViewById(R.id.iv_regiao);
            tv_descricao = (TextView) itemView.findViewById(R.id.tv_descricao);
            tv_nome_regiao = (TextView) itemView.findViewById(R.id.tv_nome_regiao);
            //tvCreatedAt = (TextView) itemView.findViewById(R.id.tv_event_created_at);
            cb_check = (CheckBox) itemView.findViewById(R.id.chk_box);
            cb_check.setOnCheckedChangeListener(this);
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

        public void CheckChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView != null){
                for (int i = 0; i < mViews.size(); i++){
                    MyViewHolder mv = mViews.get(i);
                    if (mv.tv_nome_regiao.getText().equals(tv_nome_regiao.getText()) ){
                        Regiao r = getItem(i);
                        if (r != null){
                            r.setChecked(isChecked);
                        }
                        checkBoxClick( isChecked );
                        break;
                    }
                }

            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            CheckChanged(buttonView, isChecked);
            if (mChangeListener != null) {
                mChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        }
    }
}
