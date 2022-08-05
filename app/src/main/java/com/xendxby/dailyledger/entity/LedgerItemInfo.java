package com.xendxby.dailyledger.entity;

import androidx.annotation.NonNull;

import java.math.BigDecimal;

public class LedgerItemInfo {
    public static enum RecordTypeEnum {
        OUTCOME,
        INCOME
    }

    public int id;
    public String time;
    public RecordTypeEnum type;

    public int getTypeIntValue() {
        return type == RecordTypeEnum.OUTCOME ? 0 : 1;
    }

    public void setTypeByIntValue(int type) {
        this.type = type == 0 ? RecordTypeEnum.OUTCOME : RecordTypeEnum.INCOME;
    }

    public int category;
    public String remark;
    public BigDecimal price;

    @NonNull
    @Override
    public String toString() {
        return "time: " + time + " type: " + type + " category: " + category + " remark: " + remark + " price: " + price;
    }

    public LedgerItemInfo(int id, String time, RecordTypeEnum type, int category, String remark, BigDecimal price) {
        super();
        this.id = id;
        this.time = new String(time);
        this.type = type;
        this.category = category;
        this.remark = new String(remark);
        this.price = BigDecimal.valueOf(price.floatValue());
    }
}
