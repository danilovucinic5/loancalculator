package com.loancalculator.dto;

public class InstallmentDTO {
    private int month;
    private double principal;
    private double interest;
    private double monthlyPayment;
    private double remainingBalance;

    public InstallmentDTO(int month, double principal, double interest, double monthlyPayment, double remainingBalance) {
        this.month = month;
        this.principal = principal;
        this.interest = interest;
        this.monthlyPayment = monthlyPayment;
        this.remainingBalance = remainingBalance;
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

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyTotal(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}
