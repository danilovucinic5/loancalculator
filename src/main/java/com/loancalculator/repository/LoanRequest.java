package com.loancalculator.repository;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;


@Table(name = "loan_request")
@Entity
public class LoanRequest {
    @Id
    @Column(name = "request_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ammount")
    private double amount;
    @Column(name = "annual_interest_rate")
    private double annualInterestRate;

    @Column(name = "duration_months")
    private int durationMonths;

    @Column(name = "created")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "loanRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LoanRequest(double amount, double annualInterestRate, int durationMonths, LocalDateTime createdAt) {
        this.amount = amount;
        this.annualInterestRate = annualInterestRate;
        this.durationMonths = durationMonths;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public void setInstallments(List<Installment> installments) {
        this.installments = installments;
    }
}
