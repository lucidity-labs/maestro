package org.example.example.config;

import org.example.engine.api.Maestro;
import org.example.example.activity.Foo;
import org.example.example.activity.MyActivity;
import org.example.example.activity.MyActivityImpl;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class Config {

    @PostConstruct
    public void init() {
        MyActivity myActivity = new MyActivityImpl(new Foo());

        Maestro.registerActivity(myActivity);
    }
}
