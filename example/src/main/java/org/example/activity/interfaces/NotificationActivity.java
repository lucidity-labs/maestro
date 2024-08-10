package org.example.activity.interfaces;

import org.example.engine.api.activity.ActivityInterface;

@ActivityInterface
public interface NotificationActivity {

    String sendOrderConfirmedEmail();

    String sendOrderShippedEmail(String trackingNumber);

    String sendSpecialOfferPushNotification();
}
