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
import org.ufam.gather4u.models.Residuo;

import java.util.ArrayList;

public class ResiduoAdapter extends RecyclerView.Adapter<ResiduoAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Residuo> mList;
    private ArrayList<MyViewHolder> mViews;
    private ClickListener mClickListener;
    private int[] mImgsResiduos = null;
    private String[] mNomeResiduos = null;
    private String[] mDescricaoResiduos = null;

    public ResiduoAdapter(Context c) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ResiduoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_residuo_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        mViews.add(vh);
        return vh;
    }

    public ArrayList<Residuo> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setList(ArrayList<Residuo> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public Residuo getItem(int position){
        return mList.get(position);
    }

    public MyViewHolder getViewHolder(int position){

        if (mViews != null){
            return mViews.get(position);
        }
        return null;
    }

    public void clickCheckBox( final int position ){
        Residuo r = getItem(position);
        MyViewHolder holder = getViewHolder(position);
        if (holder != null) {
            Boolean chk = !holder.getCheckBoxIsChecked();
            r.setChecked(chk);
            holder.checkBoxClick(chk);
        }
    }

    @Override
    public void onBindViewHolder(ResiduoAdapter.MyViewHolder holder, final int position) {

        //holder.tvCreatedAt.setText(DateHandle.prettyDate(mList.get(position).getCreatedAt()));

        int resourceId = R.drawable.ic_information_outline;

        mImgsResiduos = new int[] {
                R.drawable.ic_lixo_azul_hdpi,
                R.drawable.ic_lixo_vermelho_hdpi,
                R.drawable.ic_lixo_amarelo_hdpi,
                R.drawable.ic_lixo_verde_hdpi,
                R.drawable.ic_lixo_marron_hdpi,
                R.drawable.ic_lixo_preto_hdpi };
        if (position < mImgsResiduos.length){
            resourceId = mImgsResiduos[position];
        }

//        mNomeResiduos = new String[] {
//                "Papel",
//                "Plástico",
//                "Metal",
//                "Vidro",
//                "Não Orgânico"
//        };
        holder.tv_nome_residuo.setText(mList.get(position).getNome());

//        mDescricaoResiduos = new String[] {
//                "Papel limpo, seco e não contaminado",
//                "Plástico limpo, seco e não contaminado",
//                "Metal limpo, seco e não contaminado",
//                "Vidro limpo, seco e não contaminado",
//                "Não Orgânicos em geral"
//        };
//        holder.tv_descricao.setText(mDescricaoResiduos[position]);

        String desc = mList.get(position).getDescricao();
        holder.tv_descricao.setText(desc);

        Picasso.with(mContext)
                .load(resourceId)
                .error(ContextCompat.getDrawable(mContext,R.drawable.ic_information_outline))
                .into(holder.iv_residuo);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            View.OnLongClickListener,
            CompoundButton.OnCheckedChangeListener{
        private TextView tv_descricao;
        private TextView tv_nome_residuo;
        //private TextView tvCreatedAt;
        private ImageView iv_residuo;
        private CheckBox cb_check;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_residuo = (ImageView) itemView.findViewById(R.id.iv_residuo);
            tv_descricao = (TextView) itemView.findViewById(R.id.tv_descricao);
            tv_nome_residuo = (TextView) itemView.findViewById(R.id.tv_nome_residuo);
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

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView != null){
                for (int i = 0; i < mViews.size(); i++){
                    MyViewHolder mv = mViews.get(i);
                    if (mv.tv_nome_residuo.getText().equals(tv_nome_residuo.getText()) ){
                        Residuo r = getItem(i);
                        if (r != null){
                            r.setChecked(isChecked);
                        }
                        checkBoxClick( isChecked );
                        break;
                    }
                }

            }
        }
    }
}