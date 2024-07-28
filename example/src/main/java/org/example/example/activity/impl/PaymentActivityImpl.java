package org.example.example.activity.impl;

import org.example.example.activity.interfaces.PaymentActivity;

public class PaymentActivityImpl implements PaymentActivity {
    @Override
    public String processPayment() {
        System.out.println("processing payment");
        return "success";
    }
}
