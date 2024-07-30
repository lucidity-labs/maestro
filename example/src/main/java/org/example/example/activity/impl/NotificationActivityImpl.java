package org.example.example.activity.impl;

import org.example.example.activity.interfaces.NotificationActivity;
import org.example.example.service.EmailService;

public class NotificationActivityImpl implements NotificationActivity {

    private final EmailService emailService;

    public NotificationActivityImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String sendOrderConfirmedEmail() {
        System.out.println("sending order confirmed email using " + emailService);
        return "some response";
    }

    @Override
    public String sendOrderShippedEmail() {
        System.out.println("sending order shipped email using " + emailService);
        return "some response";
    }

    @Override
    public String sendSpecialOfferEmail() {
        System.out.println("sending special offer email using " + emailService);
        return "some response";
    }
}
