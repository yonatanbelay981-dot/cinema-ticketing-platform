package com.cinema.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
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
        // Here you would implement the actual email sending logic using an email library or service.

    }
}
