package org.example.event;

public record UserEvent(Operation operation, String email) {
    public enum Operation { CREATED, DELETED }
}