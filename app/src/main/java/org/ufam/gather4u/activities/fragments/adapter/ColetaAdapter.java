package org.ufam.gather4u.activities.fragments.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.Constants.HttpMessageType;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.models.Entrega;

import java.util.ArrayList;

public class ColetaAdapter extends RecyclerView.Adapter<ColetaAdapter.MyViewHolder>
    implements CustomHttpResponse{

    public String TAG =  this.getClass().getSimpleName();

    private RecyclerView rv = null;
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<Entrega> mList = null;
    private ArrayList<MyViewHolder> mViews;
    private ClickListener mClickListener = null;

    public void setAux(RecyclerView rv) {
        this.rv = rv;
    }

    public ColetaAdapter(LayoutInflater layoutInflater) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mLayoutInflater = layoutInflater;
    }

    @Override
    public ColetaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_entrega_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        mViews.add(vh);
        return vh;
    }

    public MyViewHolder getViewHolder(int position){
        if (mViews != null){
            return  mViews.get(position);
        }
        return null;
    }

    public ArrayList<Entrega> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setList(ArrayList<Entrega> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public Entrega getItem(int position){
        return mList.get(position);
    }

    @Override
    public void onBindViewHolder(ColetaAdapter.MyViewHolder holder, final int position) {

        if (position < mList.size()){
            String nome = mList.get(position).getEntregador().trim();
            holder.tv_nome.setText(nome);
            String desc = mList.get(position).getLogradouro().trim();
            holder.tv_logrdouro.setText(desc);
            String dtentrega = mList.get(position).getDTCadstro().trim();
            holder.tv_dtcad.setText(dtentrega);
            String peso = mList.get(position).getPesoTotal().trim();
            holder.tv_peso.setText(peso);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int getViewsCount() {
        return mViews.size();
    }

    @Override
    public void OnHttpResponse(JSONObject response, HttpMessageType flag) {
        if (response != null) {
            try {
                JSONArray list = response.getJSONArray(Constants.POSTS);
                if (response.has("data")) {

                    if (!response.getString("data").contains("Erro:")) {

                        switch (flag) {
                            case ENTREGASNOVAS:
                            case ENTREGASACEITAS:
                            case ENTREGASFINALIZADAS:

                                try {
                                    ArrayList<Entrega> items = new ArrayList<>();
                                    for (int i = 0; i < list.length(); i++) {
                                        Entrega r = new Entrega();
                                        JSONObject joEntrega = list.getJSONObject(i);

                                        r.setId(joEntrega.getInt("id"));
                                        r.setIdEntregador(joEntrega.getInt("identregador"));
                                        r.setIdUsuario(joEntrega.getInt("idusuario"));
                                        r.setEntregador(joEntrega.getString("entregador"));
                                        r.setLogradouro(joEntrega.getString("logradouro"));
                                        r.setDTCadstro(joEntrega.getString("dtcadastro"));
                                        r.setPesoTotal(joEntrega.getString("pesototal"));
                                        r.setPontos(joEntrega.getString("pontos"));
                                        r.setTipoResidencia(joEntrega.getString("tiporesidencia"));
                                        r.setBairro(joEntrega.getString("bairro"));
                                        r.setObs(joEntrega.getString("observacao"));
                                        items.add(r);
                                    }
                                    setList(items);
                                    Log.e(TAG, "size: " + items.size());
                                    if (rv != null){
                                        EntregaAdapter adap = (EntregaAdapter) rv.getAdapter();
                                        rv.setHasFixedSize(true);
//                                        rv.setItemAnimator(new DefaultItemAnimator());
                                        rv.setNestedScrollingEnabled(true);
                                        rv.setAdapter(adap);
                                        rv.invalidate();
                                        rv.smoothScrollToPosition(mList.size());
                                    }

                                } catch (Exception ex) {
                                }
                                break;
                        } // switch
                    }
                }
            }catch (Exception ex){

            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView iv_image;
        private TextView tv_nome;
        private TextView tv_logrdouro;
        private TextView tv_peso;
        private TextView tv_dtcad;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_nome = (TextView) itemView.findViewById(R.id.tv_nome);
            tv_logrdouro = (TextView) itemView.findViewById(R.id.tv_logradouro);
            tv_peso = (TextView) itemView.findViewById(R.id.tv_peso);
            tv_dtcad = (TextView) itemView.findViewById(R.id.tv_dtcad);
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