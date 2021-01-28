package io.github.battlepass.exceptions;

public class PassNotFoundException extends RuntimeException {

    public PassNotFoundException(String passId) {
        super(String.format("Could not find a pass with the ID %s. The ID must be either 'free' or 'premium'", passId));
    }
}
