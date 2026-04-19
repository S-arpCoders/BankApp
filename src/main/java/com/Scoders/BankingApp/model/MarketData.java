package com.Scoders.BankingApp.model;

import java.time.LocalDateTime;

public class MarketData {
    private String symbol;
    private String name;
    private String type;
    private double price;
    private double change;
    private double changePercent;
    private double high;
    private double low;
    private double open;
    private LocalDateTime lastUpdate;

    public MarketData() {
    }

    public MarketData(String symbol, String name, String type, double price, double change, double changePercent, double high, double low, double open) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
        this.high = high;
        this.low = low;
        this.open = open;
        this.lastUpdate = LocalDateTime.now();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isPositive() {
        return change >= 0;
    }
}
