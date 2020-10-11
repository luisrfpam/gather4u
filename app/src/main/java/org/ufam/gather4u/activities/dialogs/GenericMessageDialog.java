package org.ufam.gather4u.activities.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.ufam.gather4u.R;
import org.ufam.gather4u.utils.TextMasks;

public class GenericMessageDialog extends DialogFragment implements View.OnClickListener {

    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void show(final Activity activity,String title, String message, String button){
        /*
         * Inicializa o Dialog com o layout customizado
         */
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_generic_message);
        dialog.setCancelable(false);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.title);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.message);
        TextView tvButton = (TextView) dialog.findViewById(R.id.btn_ok);

        if(tvButton!=null){
            tvButton.setOnClickListener(this);
        }

        tvTitle.setText(title);
        tvMessage.setText(TextMasks.getHtmlFormated(message));
        tvButton.setText(button);

        try{
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                dialog.dismiss();
                break;

        }
    }
}
