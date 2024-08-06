package org.example.example.activity.interfaces;

import org.example.engine.api.activity.ActivityInterface;

import java.math.BigDecimal;

@ActivityInterface
public interface PaymentActivity {

    String processPayment(BigDecimal amount);
}
