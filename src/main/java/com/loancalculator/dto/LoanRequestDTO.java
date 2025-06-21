package com.loancalculator.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class LoanRequestDTO {
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Amount must not exceed 1,000,000")
    private double amount;

    @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Interest rate must be realistic (0-100%)")

    private double annualInterestRate;
    @Min(value = 1, message = "Duration (months) must be at least 1")
    @Max(value = 600, message = "Duration must be realistic (max 50 years)")
    private int durationMonths;

    public LoanRequestDTO(double amount, double annualInterestRate, int durationMonths) {
        this.amount = amount;
        this.annualInterestRate = annualInterestRate;
        this.durationMonths = durationMonths;
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

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }
}
