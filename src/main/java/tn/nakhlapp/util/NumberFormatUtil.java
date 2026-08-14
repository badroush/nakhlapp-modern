package tn.nakhlapp.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumberFormatUtil {

    private static final DecimalFormat THREE_DECIMALS;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        THREE_DECIMALS = new DecimalFormat("##0.000", symbols);
    }

    private NumberFormatUtil() {
    }

    public static String format3(double value) {
        return THREE_DECIMALS.format(value);
    }

    public static double parseDouble(String value, double defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
