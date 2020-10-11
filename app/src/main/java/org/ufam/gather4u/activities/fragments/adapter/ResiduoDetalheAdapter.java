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
import org.ufam.gather4u.models.Residuo;

import java.util.ArrayList;

public class ResiduoDetalheAdapter extends RecyclerView.Adapter<ResiduoDetalheAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Residuo> mList;
    private ArrayList<MyViewHolder> mViews;
    private ClickListener mClickListener;
    private int[] mImgsResiduos = null;

    public ResiduoDetalheAdapter(Context c) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ResiduoDetalheAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_detalhe_residuo_list, parent, false);
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

    @Override
    public void onBindViewHolder(ResiduoDetalheAdapter.MyViewHolder holder, final int position) {
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

        holder.tv_nome_residuo.setText(mList.get(position).getNome());
        holder.tv_peso.setText( mList.get(position).getPeso());
        holder.tv_pontos.setText( mList.get(position).getPesoPontuacao());

        Picasso.with(mContext)
                .load(resourceId)
                .error(ContextCompat.getDrawable(mContext,R.drawable.ic_information_outline))
                .into(holder.iv_residuo);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_residuo;
        private TextView tv_nome_residuo;
        public TextView tv_peso;
        private TextView tv_pontos;


        public MyViewHolder(View itemView) {
            super(itemView);
            iv_residuo = (ImageView) itemView.findViewById(R.id.iv_residuo);
            tv_nome_residuo = (TextView) itemView.findViewById(R.id.tv_nome_residuo);
            tv_peso = (TextView) itemView.findViewById(R.id.tv_peso);
            tv_pontos = (TextView) itemView.findViewById(R.id.tv_pontos);

        }
    }
}