package com.foodpanda.urbanninja.model;

import org.joda.time.DateTime;

public class CashReport {
    private String code;
    private String name;
    private double value;
    private DateTime dateTime;
    private boolean isCanceled;
    private boolean isLastAction;

    public CashReport(String code, String name, double value, DateTime dateTime, boolean isCanceled, boolean isLastAction) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.dateTime = dateTime;
        this.isCanceled = isCanceled;
        this.isLastAction = isLastAction;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public boolean isLastAction() {
        return isLastAction;
    }
}
