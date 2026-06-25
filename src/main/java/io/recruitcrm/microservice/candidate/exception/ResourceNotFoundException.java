package io.recruitcrm.microservice.candidate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested entity cannot be located. Mapped to HTTP 404 when it
 * escapes a controller.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Build an exception with a uniform {@code "<resource> with id <id> not found"} message.
     *
     * @param resource the entity type, e.g. {@code "Candidate"}
     * @param id       the identifier that was not found
     * @return a new exception instance
     */
    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException(resource + " with id " + id + " not found");
    }
}
