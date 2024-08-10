package org.example.config;

import jakarta.annotation.PostConstruct;
import org.example.engine.api.Maestro;
import org.example.engine.api.activity.ActivityOptions;
import org.example.activity.impl.InventoryActivityImpl;
import org.example.activity.impl.NotificationActivityImpl;
import org.example.activity.impl.PaymentActivityImpl;
import org.example.service.EmailService;
import org.example.workflow.OrderWorkflowImpl;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class Config {

    private final EmailService emailService;

    public Config(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        Maestro.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);

        Maestro.registerActivity(new InventoryActivityImpl());
        Maestro.registerActivity(new NotificationActivityImpl(emailService));
        Maestro.registerActivity(
                new PaymentActivityImpl(),
                new ActivityOptions(Duration.ofMinutes(1)) // Activity will be retried if it hasn't completed one minute after starting
        );
    }
}
