package io.github.jonloucks.example.server;

import static io.github.jonloucks.contracts.api.Checks.messageCheck;

/**
 * Runtime exception thrown for Server related problems.
 * For example, when Server fails to initialize.
 */
public class ServerException extends RuntimeException {
    
    private static final long serialVersionUID = 0L;
    
    /**
     * Passthrough for {@link RuntimeException#RuntimeException(String)}
     *
     * @param message the message for this exception
     */
    public ServerException(String message) {
        this(message, null);
    }
    
    /**
     * Passthrough for {@link RuntimeException#RuntimeException(String, Throwable)}
     *
     * @param message the message for this exception
     * @param thrown  the cause of this exception, null is allowed
     */
    public ServerException(String message, Throwable thrown) {
        super(messageCheck(message), thrown);
    }
}
