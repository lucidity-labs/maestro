package org.example.activity.interfaces;

import lucidity.maestro.engine.api.activity.ActivityInterface;

@ActivityInterface
public interface NotificationActivity {

    String sendOrderConfirmedEmail();

    String sendOrderShippedEmail(String trackingNumber);

    String sendSpecialOfferPushNotification();
}
