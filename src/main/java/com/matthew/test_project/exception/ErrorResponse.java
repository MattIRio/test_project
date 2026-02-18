package com.matthew.test_project.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path,
        Map<String, String> errors
) {
    public ErrorResponse(int status, String error, String message, Instant timestamp, String path) {
        this(status, error, message, timestamp, path, null);
    }
}
