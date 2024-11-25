package com.Scoders.BankingApp.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transId;
    
    private Double amount;
    private LocalDateTime dateTime;
    
    @ManyToOne
    @JoinColumn(name = "accNo", referencedColumnName = "accNo")
    private Account account;  // AccNo Foreign Key relationship
    
    private String transactionType;

    // Getters and Setters
    public Long getTransId() {
        return transId;
    }

    public void setTransId(Long transId) {
        this.transId = transId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
