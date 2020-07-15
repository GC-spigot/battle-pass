package io.github.battlepass.logger.containers;

public class BasicContainer extends LogContainer {
    private final String message;

    public BasicContainer(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
