package com.loancalculator;

import com.loancalculator.dto.InstallmentDTO;
import com.loancalculator.dto.LoanResponseDTO;
import com.loancalculator.repository.LoanRequestRepository;
import com.loancalculator.service.LoanCalculatorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LoanCalculatorBoundaryTest {
    @Mock
    private LoanRequestRepository repository;

    @InjectMocks
    private LoanCalculatorServiceImpl service;

    @Test
    void testLoanCalculationMinimumAmount() {
        double amount = 0.01;
        double rate = 1.0;
        int months = 2;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(2, installments.size(), "Should generate 2 installments");

        // First installment
        InstallmentDTO first = installments.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(0.01, first.getMonthlyPayment(), 0.01);
        assertEquals(0.01, first.getPrincipal(), 0.01);
        assertEquals(0.00, first.getInterest(), 0.01);
        assertEquals(0.00, first.getRemainingBalance(), 0.01);

        // Second installment
        InstallmentDTO second = installments.get(1);
        assertEquals(2, second.getMonth());
        assertEquals(0.00, second.getMonthlyPayment(), 0.01);
        assertEquals(0.00, second.getPrincipal(), 0.01);
        assertEquals(0.00, second.getInterest(), 0.01);
        assertEquals(0.00, second.getRemainingBalance(), 0.01);
    }

    @Test
    void testLoanCalculationMaximumAmount() {
        double amount = 1_000_000.00;
        double rate = 10.0;
        int months = 5;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(5, installments.size(), "Should generate 5 installments");

        // First installment
        InstallmentDTO first = installments.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(205_027.66, first.getMonthlyPayment(), 0.01);
        assertEquals(196_694.33, first.getPrincipal(), 0.01);
        assertEquals(8_333.33, first.getInterest(), 0.01);
        assertEquals(803_305.67, first.getRemainingBalance(), 0.01);

        // Last installment
        InstallmentDTO last = installments.get(4);
        assertEquals(5, last.getMonth());
        assertEquals(205_027.66, last.getMonthlyPayment(), 0.01);
        assertEquals(203_333.22, last.getPrincipal(), 0.01);
        assertEquals(1_694.44, last.getInterest(), 0.01);
        assertEquals(0.00, last.getRemainingBalance(), 0.01);
    }

    @Test
    void testLoanCalculationOneMonthDuration() {
        double amount = 500.00;
        double rate = 5.0;
        int months = 1;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(1, installments.size(), "Should generate 1 installment");

        InstallmentDTO installment = installments.get(0);
        assertEquals(1, installment.getMonth());
        assertEquals(502.08, installment.getMonthlyPayment(), 0.01);
        assertEquals(500.00, installment.getPrincipal(), 0.01);
        assertEquals(2.08, installment.getInterest(), 0.01);
        assertEquals(0.00, installment.getRemainingBalance(), 0.01);
    }

    @Test
    void testLoanCalculationLongDuration() {
        double amount = 5000.00;
        double rate = 5.0;
        int months = 600;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(600, installments.size(), "Should generate 600 installments");

        // First installment
        InstallmentDTO first = installments.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(22.71, first.getMonthlyPayment(), 0.01);
        assertEquals(1.88, first.getPrincipal(), 0.01);
        assertEquals(20.83, first.getInterest(), 0.01);
        assertEquals(4998.12, first.getRemainingBalance(), 0.01);

        // Last installment
        InstallmentDTO last = installments.get(599);
        assertEquals(600, last.getMonth());
        assertEquals(14.59, last.getMonthlyPayment(), 0.01);
        assertEquals(14.53, last.getPrincipal(), 0.01);
        assertEquals(0.06, last.getInterest(), 0.01);
        assertEquals(0.00, last.getRemainingBalance(), 0.01);
    }

    @Test
    void testLoanCalculationMinInterestRate() {
        double amount = 500.0;
        double rate = 0.01;
        int months = 5;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(5, installments.size(), "Should generate 5 installments");

        // Check first installment
        InstallmentDTO first = installments.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(100.00, first.getMonthlyPayment(), 0.01);
        assertEquals(100.00, first.getPrincipal(), 0.01);
        assertEquals(0.00, first.getInterest(), 0.01);
        assertEquals(400.00, first.getRemainingBalance(), 0.01);

        // Check last installment
        InstallmentDTO last = installments.get(4);
        assertEquals(5, last.getMonth());
        assertEquals(100.00, last.getMonthlyPayment(), 0.01);
        assertEquals(100.00, last.getPrincipal(), 0.01);
        assertEquals(0.00, last.getInterest(), 0.01);
        assertEquals(0.00, last.getRemainingBalance(), 0.01);
    }

    @Test
    void testLoanCalculationMaxInterestRate() {
        double amount = 5000.0;
        double rate = 100.0;
        int months = 5;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(5, installments.size(), "Should generate 5 installments");

        // First installment
        InstallmentDTO first = installments.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(1263.30, first.getMonthlyPayment(), 0.01);
        assertEquals(846.63, first.getPrincipal(), 0.01);
        assertEquals(416.67, first.getInterest(), 0.01);
        assertEquals(4153.37, first.getRemainingBalance(), 0.01);

        // Last installment
        InstallmentDTO last = installments.get(4);
        assertEquals(5, last.getMonth());
        assertEquals(1263.32, last.getMonthlyPayment(), 0.01);
        assertEquals(1166.14, last.getPrincipal(), 0.01);
        assertEquals(97.18, last.getInterest(), 0.01);
        assertEquals(0.00, last.getRemainingBalance(), 0.01);
    }

    @Test
    void testLoanCalculationMaxAmount() {
        double amount = 1000000;
        double rate = 5;
        int months = 5;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(5, installments.size(), "Should generate 5 installments");

        // First installment
        InstallmentDTO first = installments.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(202506.93, first.getMonthlyPayment(), 0.01);
        assertEquals(198340.26, first.getPrincipal(), 0.01);
        assertEquals(4166.67, first.getInterest(), 0.01);
        assertEquals(801659.74, first.getRemainingBalance(), 0.01);

        // Last installment
        InstallmentDTO last = installments.get(4);
        assertEquals(5, last.getMonth());
        assertEquals(202506.94, last.getMonthlyPayment(), 0.01);
        assertEquals(201666.66, last.getPrincipal(), 0.01);
        assertEquals(840.28, last.getInterest(), 0.01);
        assertEquals(0.00, last.getRemainingBalance(), 0.01);
    }
}
