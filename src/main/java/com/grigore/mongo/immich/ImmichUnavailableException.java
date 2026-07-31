package com.grigore.mongo.immich;

/**
 * Thrown when Immich isn't configured, isn't reachable, or rejects a request.
 * Distinct from the app's own data errors (UserNotFoundException /
 * IllegalArgumentException) since this is a third-party integration problem,
 * not something wrong with the app's own data.
 */
public class ImmichUnavailableException extends RuntimeException {
    public ImmichUnavailableException(String message) {
        super(message);
    }
}
