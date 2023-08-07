package com.inn.cafe.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Slf4j
@Service
public class EmailUtils {

    @Autowired
    JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> adminsList){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("tawandabanda93@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);

        if(adminsList != null && adminsList.size() > 0)
            simpleMailMessage.setCc(ccList(adminsList));
        log.info("Email has been sent...");
//        emailSender.send(simpleMailMessage);
    }

    public String[] ccList(List<String> ccList){
        String[] cc = new String[ccList.size()];

        for(int i = 0; i < ccList.size(); i++){
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    public void forgotPassword(String to, String subject) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setFrom("tawandabanda93@gmail.com");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(to);

        String htmlMessage = "<p><b>You have forgotten your details for Cafe Management System </b><br><b>Email: </b> " + to +
                "<br><b> Change your password on this link " +
                "<a href = \"http://localhost:8080/user/forgotPasswordUpdate"+ "</p>";
        message.setContent(htmlMessage, "text/html");
//        emailSender.send(message);
    }

    public void passwordChanged(String to, String subject) throws MessagingException{
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setFrom("tawandabanda93@gmail.com");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(to);
        message.setContent("Password successfully changed", "text/html");
//        emailSender.send(message);
    }

}
