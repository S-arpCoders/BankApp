package com.Scoders.BankingApp.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accNo;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;  // UserID Foreign Key relationship
    
    private Double balance;

    // Getters and Setters
    public Long getAccNo() {
        return accNo;
    }

    public void setAccNo(Long accNo) {
        this.accNo = accNo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
