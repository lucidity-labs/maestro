package org.example.example.activity.impl;

import org.example.example.activity.interfaces.NotificationActivity;

public class NotificationActivityImpl implements NotificationActivity {
    @Override
    public String sendOrderConfirmedEmail() {
        System.out.println("sending order confirmed email");
        return "some response";
    }

    @Override
    public String sendOrderShippedEmail() {
        System.out.println("sending order shipped email");
        return "some response";
    }

    @Override
    public String sendSpecialOfferEmail() {
        System.out.println("sending special offer email");
        return "some response";
    }
}
