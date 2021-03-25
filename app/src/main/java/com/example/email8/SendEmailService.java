package com.example.email8;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
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
import javax.mail.util.ByteArrayDataSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.ImageView;

public class SendEmailService {
    private static SendEmailService instance = null;
    private static Context ctx;

    final String username = "capstonemailtest@gmail.com";
    final String password = "**********";

    Properties prop;
    Session session;

    static final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    private SendEmailService(Context context) {
        ctx = context;

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        prop = new Properties();

        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.host", "smtp.gmail.com");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");
        prop.setProperty("mail.smtp.quitwait", "false");

        session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public static synchronized SendEmailService getInstance(Context context) {
        if(instance == null) {
            instance = new SendEmailService(context);
        }
        return instance;
    }

    public void SendEmail() {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("capstonemailtest@gmail.com")
            );
            message.setSubject("kill me");
            Multipart multipart = new MimeMultipart();

            //text
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<H1>i hate this!</H1>";
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);


            //attachment
            MimeBodyPart textBodyPart = new MimeBodyPart();
            ByteArrayDataSource tds = new ByteArrayDataSource("testtesttest".getBytes(Charset.forName("UTF-8")), "text/plain");
            textBodyPart.setDataHandler(new DataHandler(tds));
            textBodyPart.setHeader("Content-ID", "<text>");
            textBodyPart.setFileName("file.txt");
            multipart.addBodyPart(textBodyPart);

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("working");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
