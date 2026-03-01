package org.example.event;

public record UserLifecycleEvent(
        Operation operation,
        String email
) {
    public enum Operation {
        CREATED, DELETED
    }
}
