package org.example.activity.impl;

import org.example.activity.interfaces.PaymentActivity;

import java.math.BigDecimal;

public class PaymentActivityImpl implements PaymentActivity {
    @Override
    public String processPayment(BigDecimal amount) {
        System.out.println("processing payment of " + amount);
        return "PROCESSED";
    }
}
