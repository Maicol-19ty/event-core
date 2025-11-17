package cue.edu.co.eventcore.domain.exceptions;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id %d not found", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s with identifier '%s' not found", resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
