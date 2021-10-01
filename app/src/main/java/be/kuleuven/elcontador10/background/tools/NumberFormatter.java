package be.kuleuven.elcontador10.background.tools;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {
    String finalNumber;
    boolean isNegative;
    long originalNumber;
    public NumberFormatter(long rawNumber) {
        originalNumber = rawNumber;
        isNegative= rawNumber < 0;

    }

    public String getFinalNumber() {
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        long editingNumber =(isNegative)?originalNumber*-1:originalNumber;
        StringBuilder formattedNumber = new StringBuilder();
        if (isNegative)formattedNumber.append("-");
        formattedNumber.append(currencyFormatter.format(editingNumber));
        finalNumber = String.valueOf(formattedNumber);

        return finalNumber;
    }

    public boolean isNegative() {
        return isNegative;
    }
    public long getOriginalNumber() {
        return originalNumber;
    }

    public void setOriginalNumber(long originalNumber) {
        this.originalNumber = originalNumber;
    }
}
