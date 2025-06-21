package com.loancalculator.repository;

import jakarta.persistence.*;

@Entity
@Table(name = "installment")
public class Installment {
    @Id
    @Column(name = "installment_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month_number")
    private int month;
    @Column(name = "principal")
    private double principal;
    @Column(name = "interest")
    private double interest;
    @Column(name = "monthly_payment")
    private double monthlyPayment;
    @Column(name = "remainingBalance")
    private double remainingBalance;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private LoanRequest loanRequest;

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public LoanRequest getLoanRequest() {
        return loanRequest;
    }

    public void setLoanRequest(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
    }

    public Installment(int month, double principal, double interest, double monthlyPayment, double remainingBalance) {
        this.month = month;
        this.principal = principal;
        this.interest = interest;
        this.monthlyPayment = monthlyPayment;
        this.remainingBalance = remainingBalance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }


}
