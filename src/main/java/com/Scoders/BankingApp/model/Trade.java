package com.Scoders.BankingApp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;
    
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accNo")
    private Account account;
    
    private String symbol;
    private String assetName;
    private String assetType;
    private String tradeType;
    private Double quantity;
    private Double price;
    private Double totalAmount;
    private LocalDateTime tradeTime;
    private String status;

    public Trade() {
    }

    public Trade(Account account, String symbol, String assetName, String assetType, 
                 String tradeType, Double quantity, Double price, Double totalAmount) {
        this.account = account;
        this.symbol = symbol;
        this.assetName = assetName;
        this.assetType = assetType;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.tradeTime = LocalDateTime.now();
        this.status = "Completed";
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(LocalDateTime tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAssetTypeDisplayName() {
        if (assetType == null) {
            return "Unknown";
        }
        return switch (assetType) {
            case "stock" -> "股票";
            case "gold" -> "黄金";
            case "future" -> "期货";
            default -> assetType;
        };
    }
    
    public boolean isBuy() {
        return "BUY".equals(tradeType);
    }
    
    public boolean isSell() {
        return "SELL".equals(tradeType);
    }
}
