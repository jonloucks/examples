package io.github.jonloucks.example.client;

import static io.github.jonloucks.contracts.api.Checks.messageCheck;

/**
 * Runtime exception thrown for Client related problems.
 * For example, when Client fails to initialize.
 */
public class ClientException extends RuntimeException {
    
    private static final long serialVersionUID = 0L;
    
    /**
     * Passthrough for {@link RuntimeException#RuntimeException(String)}
     *
     * @param message the message for this exception
     */
    public ClientException(String message) {
        this(message, null);
    }
    
    /**
     * Passthrough for {@link RuntimeException#RuntimeException(String, Throwable)}
     *
     * @param message the message for this exception
     * @param thrown  the cause of this exception, null is allowed
     */
    public ClientException(String message, Throwable thrown) {
        super(messageCheck(message), thrown);
    }
}
