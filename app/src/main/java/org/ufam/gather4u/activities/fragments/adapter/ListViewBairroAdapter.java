package org.ufam.gather4u.activities.fragments.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import java.util.List;

public class ListViewBairroAdapter extends ArrayAdapter<Bairro> implements View.OnClickListener, CustomHttpResponse{
    public String TAG =  this.getClass().getSimpleName();

    private Context mContext;
    private List<Bairro> mItems = null;
    private LayoutInflater mLayoutInflater = null;
    private TextView tv_info_bairro;
    private TextView tv_nome_bairro;
    private CheckBox cb_check;
    private int mResource = -1;

    private ClickListener mClickListener = null;

    public ListViewBairroAdapter(Context context, int layout, List<Bairro> items) {
        super(context, layout, items);
        mContext = context;
        mItems = items;
        mResource = layout;
    }

    public void setLayoutInflater( LayoutInflater layoutInflater ){
        mLayoutInflater = layoutInflater;
    }

    public void setList(ArrayList<Bairro> resource)  {
        this.mItems = resource;
        notifyDataSetChanged();
    }

    public void setClickListener(ClickListener clickListener){
        this.mClickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onClickListener(v, v.getId() );
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView =  mLayoutInflater.inflate(mResource, parent, false);
//        mLayoutInflater = ((Activity)mContext).getLayoutInflater();
//        convertView = mLayoutInflater.inflate(R.layout.item_bairro_list, parent, false);

        tv_nome_bairro = (TextView) convertView.findViewById(R.id.tv_nome_bairro);
        tv_info_bairro = (TextView) convertView.findViewById(R.id.tv_info_bairro);
        cb_check = (CheckBox) convertView.findViewById(R.id.chk_box);

        convertView.setOnClickListener(this);

        if (position < mItems.size()){
            String nomeBairro = mItems.get(position).getNome().trim();
            tv_nome_bairro.setText(nomeBairro);
            String infoBairro = "Bairro " + nomeBairro;
            tv_info_bairro.setText(infoBairro);

            if(mItems.get(position).getChecked())
                cb_check.setChecked(true);
            else
                cb_check.setChecked(false);
        }

        return convertView;
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
//                                    if (rv != null){
//                                        BairroAdapter adap = (BairroAdapter) rv.getAdapter();
//                                        rv.setHasFixedSize(true);
////                                        rv.setItemAnimator(new DefaultItemAnimator());
//                                        rv.setNestedScrollingEnabled(true);
//                                        rv.setAdapter(adap);
//                                        rv.invalidate();
//                                    }

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
//                                    if (rv != null){
//
//                                        BairroAdapter adap = (BairroAdapter) rv.getAdapter();
//                                        rv.setHasFixedSize(true);
//                                        rv.setItemAnimator(new DefaultItemAnimator());
//                                        rv.setNestedScrollingEnabled(false);
//                                        rv.setAdapter(adap);
//                                        rv.invalidate();
//                                    }

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
}