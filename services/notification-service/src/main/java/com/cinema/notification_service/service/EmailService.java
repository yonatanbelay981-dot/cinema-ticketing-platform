package com.cinema.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendEmail(
            String to,
            String subject,
            String message
    ){
        log.info(
                "Sending email. to={}, subject={}, message={}",
                to,
                subject,
                message
        );
        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setFrom(from);
            mailMessage.setText(message);

            javaMailSender.send(mailMessage);


            log.info(
                    "Email sent successfully. to={}",
                    to
            );

        }catch (Exception e ){
            log.error(
                    "Failed to send email. to={}, subject={}",
                    to,
                    subject,
                    e
            );
            throw e;
        }



    }
}
