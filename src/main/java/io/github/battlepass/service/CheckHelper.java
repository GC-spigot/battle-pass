package io.github.battlepass.service;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

@UtilityClass
public class CheckHelper {

    @Contract("null, _ -> fail")
    public static void notNull(Object arg, String identifier) {
        if (arg == null) {
            throw new IllegalArgumentException(identifier.concat(" cannot be null."));
        }
    }

    @Contract("!null, _ -> fail")
    public static void isNull(Object arg, String message) {
        if (arg != null) {
            throw new IllegalArgumentException(message);
        }
    }
}
