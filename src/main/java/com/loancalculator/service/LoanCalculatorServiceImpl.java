package com.loancalculator.service;

import com.loancalculator.dto.InstallmentDTO;
import com.loancalculator.dto.LoanResponseDTO;
import com.loancalculator.repository.Installment;
import com.loancalculator.repository.LoanRequest;
import com.loancalculator.repository.LoanRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanCalculatorServiceImpl implements LoanCalculatorService {
    private final LoanRequestRepository repository;

    @Autowired
    public LoanCalculatorServiceImpl(LoanRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LoanResponseDTO calculateAndSaveLoan(double amount, double annualInterestRate, int months) {

        BigDecimal loanAmount = BigDecimal.valueOf(amount);
        BigDecimal annualRate = BigDecimal.valueOf(annualInterestRate);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal monthlyPayment;
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = loanAmount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        } else {
            // Calculate monthly payment: P = A * r / (1 - (1 + r)^-n)
            BigDecimal onePlusRatePow = BigDecimal.ONE.add(monthlyRate)
                    .pow(-months, new MathContext(20, RoundingMode.HALF_UP)); // (1 + r)^-n
            monthlyPayment = loanAmount.multiply(monthlyRate)
                    .divide(BigDecimal.ONE.subtract(onePlusRatePow), 10, RoundingMode.HALF_UP);
            monthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);
        }

        LoanRequest loanRequest = new LoanRequest(amount, annualInterestRate, months, LocalDateTime.now());
        List<Installment> installments = new ArrayList<>();
        LoanResponseDTO response = new LoanResponseDTO();
        BigDecimal remainingBalance = loanAmount;
        BigDecimal totalPrincipalPaid = BigDecimal.ZERO;

        for (int i = 1; i <= months; i++) {
            BigDecimal interest = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = monthlyPayment.subtract(interest).setScale(2, RoundingMode.HALF_UP);

            if (i == months) {
                principal = loanAmount.subtract(totalPrincipalPaid).setScale(2, RoundingMode.HALF_UP);
                monthlyPayment = principal.add(interest).setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal newBalance = remainingBalance.subtract(principal).setScale(2, RoundingMode.HALF_UP);

            Installment installment = new Installment(
                    i,
                    principal.doubleValue(),
                    interest.doubleValue(),
                    monthlyPayment.doubleValue(),
                    newBalance.doubleValue()
            );
            installment.setLoanRequest(loanRequest);

            InstallmentDTO installmentDTO = new InstallmentDTO(
                    i,
                    principal.doubleValue(),
                    interest.doubleValue(),
                    monthlyPayment.doubleValue(),
                    newBalance.doubleValue()
            );

            response.getInstallments().add(installmentDTO);
            installments.add(installment);
            totalPrincipalPaid = totalPrincipalPaid.add(principal);
            remainingBalance = newBalance;
        }

        loanRequest.setInstallments(installments);
        repository.save(loanRequest);
        return response;
    }
}
