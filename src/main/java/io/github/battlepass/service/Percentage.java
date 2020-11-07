package io.github.battlepass.service;

import io.github.battlepass.lang.Lang;
import lombok.experimental.UtilityClass;
import me.hyfe.simplespigot.text.Text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

@UtilityClass
public class Percentage {

    public static String getPercentage(BigInteger progress, BigInteger requiredProgress) {
        return new BigDecimal(progress).divide(new BigDecimal(requiredProgress), MathContext.DECIMAL32).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).toString(); // TODO probs broken
    }

    public static String getProgressBar(BigInteger progress, BigInteger requiredProgress, Lang lang) {
        float progressFloat = new BigDecimal(progress).divide(new BigDecimal(requiredProgress), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_EVEN).floatValue();
        float complete = 30 * progressFloat;
        float incomplete = 30 - complete;
        String progressBar = Text.modify(lang.external("progress-bar.complete-color").asString());
        for (int i = 0; i < complete; i++) {
            progressBar = progressBar.concat(lang.external("progress-bar.symbol").asString());
        }
        progressBar = progressBar.concat(lang.external("progress-bar.incomplete-color").asString());
        for (int i = 0; i < incomplete; i++) {
            progressBar = progressBar.concat(lang.external("progress-bar.symbol").asString());
        }
        return progressBar;
    }
}
