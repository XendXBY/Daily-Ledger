package com.xendxby.dailyledger.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    public static BigDecimal floatToBigDecimal(float f) {
        return (new BigDecimal(String.valueOf(f))).divide(new BigDecimal("1"), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal stringToBigDecimal(String s) {
        return (new BigDecimal(s)).divide(new BigDecimal("1"), 2, RoundingMode.HALF_UP);
    }

    public static String bigDecimalToString(BigDecimal bd) {
        String s = String.valueOf(bd.divide(new BigDecimal("1"), 2, RoundingMode.HALF_UP).doubleValue());
        int dotIndex = s.indexOf('.');

        if (dotIndex == -1) {
            return s + ".00";
        } else if (dotIndex == s.length() - 2) {
            return s + "0";
        }
        return s.substring(0, dotIndex + 3);
    }

}
