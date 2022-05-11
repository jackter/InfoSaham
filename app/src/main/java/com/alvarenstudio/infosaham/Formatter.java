package com.alvarenstudio.infosaham;

import java.text.DecimalFormat;

public class Formatter {
    public Formatter() {
    }

    public String pctFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount * 100) + "%";
    }

    public String currencyFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    public String decimalFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.0000");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }
}
