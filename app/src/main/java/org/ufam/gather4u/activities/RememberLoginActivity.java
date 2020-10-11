package org.ufam.gather4u.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ufam.gather4u.R;
import org.ufam.gather4u.interfaces.CustomEmailInterface;
import org.ufam.gather4u.utils.EmailSender;

public class RememberLoginActivity extends BaseActivity implements View.OnClickListener, CustomEmailInterface {

    private Button mSendData = null;
    private TextView msgRemember = null;
    private int userType = -1;
    private String userLogin = null;
    private String userPass = null;
    private String userEmail = null;
    private EmailSender eSender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rememberlogin);

        mSendData = (Button) findViewById(R.id.send_dados_button);
        msgRemember = (TextView) findViewById(R.id.rememberMsg);
        mSendData.setOnClickListener(this);

        if(getIntent().getExtras()!=null){
            userType = getIntent().getIntExtra("usertype",-1);
            userLogin = getIntent().getStringExtra("login");
            userPass = getIntent().getStringExtra("senha");
            userEmail = getIntent().getStringExtra("email");
        }

        int posAr = userEmail.indexOf('@');
        String strMsg = "Estaremos enviando os dados de login ao email: ***" +
                userEmail.substring(posAr - 4) + ".";
        msgRemember.setText(strMsg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_dados_button:
                SendEmail();
                break;

            case R.id.btn_close:
                onBackPressed();
                break;
        }
    }

    private void SendEmail(){
        try {

            showLoadingDialog("Enviando e-mail ...");

            eSender = new EmailSender();
            String msgBody = "Seus dados para login de acesso:\r\n\n\n" +

                    "Login: " + userLogin + "\r\n" +
                    "Senha: " + userPass + "\r\n\n\n" +

                    "Acesse agora mesmo o aplicatiovo Gather4u.";
            eSender.setListener(this);
            eSender.sendMessage(userEmail, msgBody);
        }
        catch(Exception ex)
        {
        }
    }

    @Override
    public void OnSentEmail() {
        dismissLoadingDialog();
        eSender.removeCallBacks();
        msgToastOk(this, "Email enviado com sucesso!", true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
