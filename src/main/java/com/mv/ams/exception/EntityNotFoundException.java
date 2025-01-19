package com.mv.ams.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, Long id) {
        super(String.format("Entity '%s' with id '%d' not found", entityType, id));
    }
}
