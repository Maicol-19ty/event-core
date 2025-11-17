package cue.edu.co.eventcore.domain.exceptions;

/**
 * Exception thrown when a business rule is violated
 */
public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
