package com.xendxby.dailyledger.entity;

import com.xendxby.dailyledger.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PriceBoardController {

    private List<Character> priceBoardInput;
    private byte decCap;
    private boolean decStart;

    public PriceBoardController() {
        super();
        priceBoardInput = new ArrayList<>();
        priceBoardInput.add('0');
        decCap = 2;
        decStart = false;
    }

    public boolean isDecStart() {
        return decStart;
    }

    public int getIntLength() {
        if (!decStart) {
            return priceBoardInput.size();
        }

        return priceBoardInput.size() - 1 - (2 - decCap);
    }

    public void push(char c) {
        if (decStart && (c == '.' || decCap <= 0)) {         // 只允许一个小数点，最多输入2位小数
            return;
        }

        if (c == '.') {
            decStart = true;
            priceBoardInput.add('.');
        } else if (priceBoardInput.size() == 1 && priceBoardInput.get(0) == '0') {     // 输入前为0的情况
            priceBoardInput.set(0, c);
        } else  {           // 输入前不为0
            if (decStart) {
                decCap--;
            }
            priceBoardInput.add(c);
        }
    }

    public void pop() {
        if (priceBoardInput.size() == 1) {
            priceBoardInput.set(0, '0');
            return;
        }

        char lastChar = priceBoardInput.get(priceBoardInput.size() - 1);

        priceBoardInput.remove(priceBoardInput.size() - 1);

        if (lastChar == '.') {
            decStart = false;
            pop();
        }

        if (decStart) {
            decCap++;
        }
    }

    public BigDecimal toBidDecimal() {
        return BigDecimalUtil.stringToBigDecimal(toString());
    }

    public double doubleValue() {
        return toBidDecimal().doubleValue();
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Character c : priceBoardInput) {
            stringBuffer.append(c.charValue());
        }
        if (!decStart) {
            stringBuffer.append(".00");
        }
        else {
            for (int i = 0; i < decCap; i++) {
                stringBuffer.append('0');
            }
        }

        return stringBuffer.toString();
    }
}
