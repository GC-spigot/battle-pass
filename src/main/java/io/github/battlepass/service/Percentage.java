package io.github.battlepass.service;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

@UtilityClass
public class Percentage {

    public static String getPercentage(BigInteger progress, BigInteger requiredProgress) {
        return new BigDecimal(progress).divide(new BigDecimal(requiredProgress), MathContext.DECIMAL32).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).toString(); // TODO probs broken
    }

    public static String getProgressBar(BigInteger progress, BigInteger requiredProgress) {
        float progressFloat = new BigDecimal(progress).divide(new BigDecimal(requiredProgress), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_EVEN).floatValue();
        float complete = 30 * progressFloat;
        float incomplete = 30 - complete;
        String progressBar = "A";
        for (int i = 0; i < complete; i++) {
            progressBar = progressBar.concat("|");
        }
        progressBar = progressBar.concat("B");
        for (int i = 0; i < incomplete; i++) {
            progressBar = progressBar.concat("|");
        }
        return progressBar;
    }
}
