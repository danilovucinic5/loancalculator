package com.loancalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    List<LoanRequest> findByAmountAndAnnualInterestRateAndDurationMonths(double amount, double interestRate, int duration);

}
