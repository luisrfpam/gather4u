package org.ufam.gather4u.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.ufam.gather4u.interfaces.CustomEmailInterface;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailTask extends AsyncTask<Void,Void,Void> {

    private String hostSMTP = "smtp.gather4u.com.br";
    private String portSMTP = "587";
    private String user = "no-reply@gather4u.com.br ";
    private String password = "Noreply1!@";

    private Session session;

    private String subject = "Gather4u - Recupere sua senha";
    private String from = "";
    private String to = "";
    private String msg = "";

    private Boolean isHtmlMessage = false;

    public Boolean getHtmlMessage() { return isHtmlMessage; }

    public void setHtmlMessage(Boolean htmlMessage) { isHtmlMessage = htmlMessage; }

    private CustomEmailInterface _listener = null;

    public EmailTask(String emailfrom, String emaildestino, String assunto, String mensagem) {
        from = emailfrom;
        to = emaildestino;
        subject = assunto;
        msg = mensagem;
    }

    public EmailTask(String emaildestino, String assunto, String mensagem) {
        from = user;
        to = emaildestino;
        if (assunto.trim().length() > 0){
            subject = assunto;
        }
        msg = mensagem;
    }

    public EmailTask(String emaildestino, String mensagem) {
        from = user;
        to = emaildestino;
        msg = mensagem;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //Showing a success message
        //Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
        if (_listener != null){
            _listener.OnSentEmail();
        }
    }

    public void setListener(CustomEmailInterface listener) {
        this._listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values

        //props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.host", hostSMTP);
        //props.put("mail.smtp.socketFactory.port", portSMTP);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", portSMTP);

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(user));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //Adding subject
            mm.setSubject(subject);

            if (isHtmlMessage){
                Multipart mp = new MimeMultipart();
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(msg, "text/html");
                mp.addBodyPart(htmlPart);
                mm.setContent(mp);
            }
            else {
                //Adding message
                mm.setText(msg);
            }

            //Sending email
            Transport.send(mm);

        } catch (MessagingException ex) {
            System.out.println(ex.getMessage());
            Log.i("Script", "Erro: " + ex.getMessage());
        }
        return null;
    }


}
