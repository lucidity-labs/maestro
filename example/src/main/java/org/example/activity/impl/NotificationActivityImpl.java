package org.example.activity.impl;

import org.example.activity.interfaces.NotificationActivity;
import org.example.service.EmailService;

public class NotificationActivityImpl implements NotificationActivity {

    private final EmailService emailService;

    public NotificationActivityImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String sendOrderConfirmedEmail() {
        System.out.println("sending order confirmed email using " + emailService);
        return "SENT";
    }

    @Override
    public String sendOrderShippedEmail(String trackingNumber) {
        System.out.println("sending order shipped email using " + emailService);
        return "SENT";
    }

    @Override
    public String sendSpecialOfferPushNotification() {
        System.out.println("sending special offer push notification");
        return "SENT";
    }
}
