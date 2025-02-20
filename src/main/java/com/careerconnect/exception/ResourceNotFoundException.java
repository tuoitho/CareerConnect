package com.careerconnect.exception;

public class ResourceNotFoundException  extends RuntimeException {
    public ResourceNotFoundException(Class<?> clazz, Long id) {
        super(clazz.getSimpleName() + " with id " + id + " not found");
    }
    public ResourceNotFoundException(Class<?> clazz, String fieldName, String fieldValue) {
        super(clazz.getSimpleName() + " with " + fieldName + " " + fieldValue + " not found");
    }
}