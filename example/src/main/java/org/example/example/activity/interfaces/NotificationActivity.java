package org.example.example.activity.interfaces;

import org.example.engine.api.annotation.ActivityInterface;

@ActivityInterface
public interface NotificationActivity {

    String sendOrderConfirmedEmail();

    String sendOrderShippedEmail();

    String sendSpecialOfferEmail();
}
