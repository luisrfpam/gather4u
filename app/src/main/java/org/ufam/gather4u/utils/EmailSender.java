package org.ufam.gather4u.utils;

import android.os.Handler;
import android.util.Log;
import org.ufam.gather4u.interfaces.CustomEmailInterface;

public class EmailSender {

    private Handler timerHandler = null;
    private String emaildestino = "";
    private String mensagem = "";
    private String emailorigem = "";
    private String assunto = "";
    private CustomEmailInterface _listener = null;

    Runnable SimpleRunnable = new Runnable() {
        @Override
        public void run() {
            EmailTask sender = new EmailTask(emaildestino, assunto, mensagem);
            sender.setListener(_listener);
            sender.execute();
        }
    };

    Runnable CompleteRunnable = new Runnable() {
        @Override
        public void run() {
            EmailTask sender = new EmailTask(emailorigem, emaildestino, assunto, mensagem);
            sender.setListener(_listener);
            sender.execute();
            timerHandler.removeCallbacks(CompleteRunnable);

        }
    };

    private void send(String emailorigem, String emaildestino, String assunto, String mensagem) throws Exception {

        try {
            timerHandler = new Handler();
            this.emailorigem = emailorigem;
            this.assunto = assunto;
            this.emaildestino = emaildestino;
            this.mensagem = mensagem;
            timerHandler.post(CompleteRunnable);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void setListener(CustomEmailInterface listener) {
        this._listener = listener;
    }

    public void sendMessage(String emaildestino,  String mensagem){
        try {

            timerHandler = new Handler();
            this.emaildestino = emaildestino;
            this.mensagem = mensagem;
            timerHandler.post(SimpleRunnable);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void sendMessage(String emaildestino, String assunto, String mensagem){
        try {

            timerHandler = new Handler();
            this.emaildestino = emaildestino;
            this.assunto = assunto;
            this.mensagem = mensagem;
            timerHandler.post(SimpleRunnable);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
    }

    public void removeCallBacks(){
        if (timerHandler != null){
            timerHandler.removeCallbacks(SimpleRunnable);
            timerHandler.removeCallbacks(CompleteRunnable);
        }
    }
}
