package org.ufam.gather4u.activities.fragments.adapter;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ufam.gather4u.R;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.application.Constants.HttpMessageType;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.interfaces.CustomHttpResponse;
import org.ufam.gather4u.models.Bairro;

import java.util.ArrayList;

public class BairroAdapter extends RecyclerView.Adapter<BairroAdapter.MyViewHolder>
    implements CustomHttpResponse{

    public String TAG =  this.getClass().getSimpleName();

    private RecyclerView rv = null;
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<Bairro> mList = null;
    private ArrayList<MyViewHolder> mViews;
    private ClickListener mClickListener = null;

    public void setAux(RecyclerView rv) {
        this.rv = rv;
    }

    public BairroAdapter(LayoutInflater layoutInflater) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mLayoutInflater = layoutInflater;
    }

    @Override
    public BairroAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_bairro_list, parent, false);
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

    public ArrayList<Bairro> getListItem(){
        return mList;
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public void setList(ArrayList<Bairro> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public Bairro getItem(int position){

        return mList.get(position);
    }

    public void clickCheckBox( final int position){
        Bairro r = getItem(position);
        MyViewHolder holder = getViewHolder(position);
        if (holder != null) {
            Boolean chk = !holder.getCheckBoxIsChecked();
            r.setChecked(chk);
            holder.checkBoxClick(chk);
        }
    }

    @Override
    public void onBindViewHolder(BairroAdapter.MyViewHolder holder, final int position) {

        if (position < mList.size()){
            String nomeBairro = mList.get(position).getNome().trim();
            holder.tv_nome_bairro.setText(nomeBairro);
            holder.tv_info_bairro.setText("Bairro " + nomeBairro);
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
                            case BAIRRO:
                                break;
                            case REGIAOBAIRRO:
                                try {
                                    ArrayList<Bairro> items = new ArrayList<>();
                                    for (int i = 0; i < list.length(); i++) {
                                        Bairro r = new Bairro();
                                        JSONObject joBairro = list.getJSONObject(i);
                                        r.setId(joBairro.getInt("id"));
                                        r.setNome(joBairro.getString("bairro"));
                                        items.add(r);
                                    }

                                    setList(items);
                                    Log.e(TAG, "size: " + items.size());
                                    if (rv != null){
                                        BairroAdapter adap = (BairroAdapter) rv.getAdapter();
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

                            case BAIRROREGIAO:
                                try {

                                    ArrayList<Bairro> items = new ArrayList<>();
                                    for (int i = 0; i < list.length(); i++) {
                                        Bairro r = new Bairro();
                                        JSONObject joBairro = list.getJSONObject(i);
                                        r.setId(joBairro.getInt("id"));
                                        r.setNome(joBairro.getString("bairro"));
                                        items.add(r);
                                    }

                                    setList(items);
                                    Log.e(TAG, "size: " + items.size());
                                    if (rv != null){

                                        BairroAdapter adap = (BairroAdapter) rv.getAdapter();
                                        rv.setHasFixedSize(true);
                                        rv.setItemAnimator(new DefaultItemAnimator());
                                        rv.setNestedScrollingEnabled(false);
                                        rv.setAdapter(adap);
                                        rv.invalidate();
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,
            CompoundButton.OnCheckedChangeListener {
        private TextView tv_info_bairro;
        private TextView tv_nome_bairro;
        private CheckBox cb_check;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_nome_bairro = (TextView) itemView.findViewById(R.id.tv_nome_bairro);
            tv_info_bairro = (TextView) itemView.findViewById(R.id.tv_info_bairro);
            cb_check = (CheckBox) itemView.findViewById(R.id.chk_box);
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
                    if (mv.tv_nome_bairro.getText().equals(tv_nome_bairro.getText()) ){
                        Bairro r = getItem(i);
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