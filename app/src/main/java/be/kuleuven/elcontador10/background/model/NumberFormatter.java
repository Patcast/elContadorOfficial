package be.kuleuven.elcontador10.background.model;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {
    String finalNumber;
    boolean isNegative;
    public NumberFormatter(long rawNumber) {
        isNegative= rawNumber < 0;
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        long editingNumber =(isNegative)?rawNumber*-1:rawNumber;
        StringBuilder formattedNumber = new StringBuilder();
        if (isNegative)formattedNumber.append("-");
        formattedNumber.append(currencyFormatter.format(editingNumber));
        finalNumber = String.valueOf(formattedNumber);
    }

    public String getFinalNumber() {
        return finalNumber;
    }

    public boolean isNegative() {
        return isNegative;
    }
}
