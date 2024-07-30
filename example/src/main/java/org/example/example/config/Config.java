package org.example.example.config;

import jakarta.annotation.PostConstruct;
import org.example.engine.api.Maestro;
import org.example.example.activity.impl.InventoryActivityImpl;
import org.example.example.activity.impl.NotificationActivityImpl;
import org.example.example.activity.impl.PaymentActivityImpl;
import org.example.example.service.EmailService;
import org.example.example.workflow.OrderWorkflowImpl;
import org.springframework.stereotype.Component;

@Component
public class Config {

    private final EmailService emailService;

    public Config(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        Maestro.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        Maestro.registerActivities(
                new InventoryActivityImpl(),
                new PaymentActivityImpl(),
                new NotificationActivityImpl(emailService)
        );
    }
}
