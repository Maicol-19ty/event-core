package cue.edu.co.eventcore.domain.exceptions;

/**
 * Exception thrown when attempting to create a duplicate resource
 */
public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String resourceName, String identifier) {
        super(String.format("%s with identifier '%s' already exists", resourceName, identifier));
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
