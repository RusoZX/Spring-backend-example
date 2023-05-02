package com.rusozx.coffeManagment.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender emailSender;

    private String from = "rusozx76@gmail.com";

    public void sendSimpleMessage(String to, String subject,String text, List<String> list){
        SimpleMailMessage msg= new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        if(list!=null && list.size()>0)
            msg.setCc(getCcArray(list));
        emailSender.send(msg);
    }

    private String[] getCcArray(List<String> ccList){
        String[] cc = new String[ccList.size()];
        for(int i = 0; i < ccList.size(); i++)
            cc[i]= ccList.get(i);
        return cc;
    }
    public void forgotMail(String to, String subject,String pwd) throws MessagingException {
        MimeMessage msg = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        msg.setContent("<p><b> Your Login details For the Coffe WebSite</b><br><b>Email:</b>"+to+
                "<br><b>Password: </b>"+pwd+"<br><a href=\"http://localhost:4200/\"> Click Here to Log In</a></p>",
                "text/html");
        emailSender.send(msg);
    }
}
