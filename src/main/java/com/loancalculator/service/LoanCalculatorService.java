package com.loancalculator.service;

import com.loancalculator.dto.LoanResponseDTO;

public interface LoanCalculatorService {
    public LoanResponseDTO calculateAndSaveLoan(double amount, double annualInterestRate, int months);
}
