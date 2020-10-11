package org.ufam.gather4u.activities.fragments.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ufam.gather4u.R;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.interfaces.ClickListener;
import org.ufam.gather4u.models.Residuo;

import java.util.ArrayList;

public class ResiduoEntregaAdapter extends RecyclerView.Adapter<ResiduoEntregaAdapter.MyViewHolder> {
    public String TAG =  this.getClass().getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Residuo> mList;
    private ArrayList<MyViewHolder> mViews;
    private ClickListener mClickListener;
    private int[] mImgsResiduos = null;
    private Dialog mMsgDialog = null;
    private int[] mDlgImgs = null;

    public ResiduoEntregaAdapter(Context c) {
        mList = new ArrayList<>();
        mViews = new ArrayList<>();
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDlgImgs = new int[] { R.drawable.ok, R.drawable.info, R.drawable.error,
                R.drawable.exclamation, R.drawable.question };
    }

    @Override
    public ResiduoEntregaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_entrega_residuo_list, parent, false);
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

    public void showMsg(String title, String message, Constants.ImageType type){

        mMsgDialog = new Dialog(mContext, R.style.FullHeightDialog);
        mMsgDialog.setContentView(R.layout.dialog_generic_message);
        TextView msgTitle = (TextView) mMsgDialog.findViewById(R.id.title);
        TextView msg = (TextView) mMsgDialog.findViewById(R.id.message);
        ImageView img = (ImageView) mMsgDialog.findViewById(R.id.dlgImage);
        Button btnOK = (Button) mMsgDialog.findViewById(R.id.btn_ok);

        msgTitle.setText(title);
        msg.setText(message);
        img.setImageResource(mDlgImgs[type.getValor()]);

        if(btnOK!=null){
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.btn_ok:
                            mMsgDialog.dismiss();
                            break;
                    }
                }
            });
        }
        mMsgDialog.show();
    }

    public void msgError(String message){ showMsg("Erro", message, Constants.ImageType.ERROR); }

    public void clickCheckBox( final int position){
        Residuo r = getItem(position);
        ResiduoEntregaAdapter.MyViewHolder holder = getViewHolder(position);
    }

    @Override
    public void onBindViewHolder(ResiduoEntregaAdapter.MyViewHolder holder, final int position) {
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

        String peso = mList.get(position).getPeso();
        if (peso != null){
            if (peso.trim().length() > 0 ){
                holder.tv_peso.setText( peso );
            }
        }

        holder.tv_nome_residuo.setText(mList.get(position).getNome());

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
            View.OnLongClickListener{

        private ImageView iv_residuo;
        private TextView tv_nome_residuo;
        public EditText tv_peso;

        //private TextView tvCreatedAt;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_residuo = (ImageView) itemView.findViewById(R.id.iv_residuo);
            tv_nome_residuo = (TextView) itemView.findViewById(R.id.tv_nome_residuo);
            tv_peso = (EditText) itemView.findViewById(R.id.tv_peso);

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