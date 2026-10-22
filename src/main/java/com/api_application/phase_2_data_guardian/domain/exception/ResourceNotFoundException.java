package com.api_application.phase_2_data_guardian.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(resourceName + " not found with id: " + resourceId);
    }
}
