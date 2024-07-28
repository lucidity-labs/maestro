package org.example.example.config;

import jakarta.annotation.PostConstruct;
import org.example.engine.api.Maestro;
import org.example.example.activity.impl.InventoryActivityImpl;
import org.example.example.activity.impl.NotificationActivityImpl;
import org.example.example.activity.impl.PaymentActivityImpl;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @PostConstruct
    public void init() {
        Maestro.registerActivities(
                new InventoryActivityImpl(),
                new PaymentActivityImpl(),
                new NotificationActivityImpl()
        );
    }
}
