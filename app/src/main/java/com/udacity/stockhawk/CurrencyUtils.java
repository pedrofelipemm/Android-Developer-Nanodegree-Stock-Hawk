package com.udacity.stockhawk;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    private static final DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    private static final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    private static final DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());

    private CurrencyUtils() {
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    public static String formatDollar(double number) {
        return dollarFormat.format(number);
    }

    public static String formatDollarWithPlus(double number){
        return dollarFormatWithPlus.format(number);
    }

    public static String formatPercentage(double number) {
        return percentageFormat.format(number);
    }
}
