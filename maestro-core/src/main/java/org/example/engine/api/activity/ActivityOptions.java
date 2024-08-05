package org.example.engine.api.activity;

import java.time.Duration;

public record ActivityOptions(Duration startedToCompletedTimeout) {
}
