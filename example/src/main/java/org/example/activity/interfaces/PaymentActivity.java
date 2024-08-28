package org.example.activity.interfaces;

import lucidity.maestro.engine.api.activity.ActivityInterface;

import java.math.BigDecimal;

@ActivityInterface
public interface PaymentActivity {

    String processPayment(BigDecimal amount);
}
