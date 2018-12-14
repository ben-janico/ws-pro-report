package com.aurea.wsproreport.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.aurea.wsproreport.logger.WsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aurea.wsproreport.config.MailSettingsConfig;
import com.aurea.wsproreport.util.StringUtil;

@Service
public class MailSenderService {

    @Autowired
    private MailSettingsConfig config;

    private List<String> recipientList;
    private String subject;
    private String body;

    public MailSenderService() {
    }

    public void sendEmail() throws IOException {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", config.getHost());
            props.put("mail.smtp.port", config.getPort());
            props.put("mail.smtp.auth", config.getAuth());
            props.put("mail.smtp.starttls.enable", config.getStarttls());

            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        @Override protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(config.getUsername(), config.getPassword());
                        }
                    });
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");

            msg.setFrom(new InternetAddress(config.getUsername()));
            msg.setReplyTo(InternetAddress.parse(config.getReplyTo()));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(body, "text/html;charset=utf-8");

            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientList.stream().collect(Collectors.joining(","))));
            if (StringUtil.nvlOrEmpty(config.getCcList(), false)) {
                msg.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(config.getCcList()));
            }
            Transport.send(msg);
        } catch (MessagingException e) {
            WsLogger.error(getClass(), e.getMessage());
        }

    }

    public List<String> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<String> recipientList) {
        this.recipientList = recipientList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
