package com.careerconnect.exception;

public class ResourceNotFoundException  extends RuntimeException {
    public ResourceNotFoundException(Class<?> clazz, String id) {
        super(clazz.getSimpleName() + " with id " + id + " not found");
    }
}