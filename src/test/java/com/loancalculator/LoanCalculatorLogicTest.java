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
class LoanCalculatorLogicTest {
    @Mock
    private LoanRequestRepository repository;

    @InjectMocks
    private LoanCalculatorServiceImpl service;

    @Test
    void testLoanCalculationMatchesExpectedInstallments() {
        double amount = 1000.0;
        double rate = 5.0;
        int months = 10;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> actual = response.getInstallments();

        assertEquals(10, actual.size(), "Should generate 10 installments");

        // Expected Installment 1
        InstallmentDTO first = actual.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(102.31, first.getMonthlyPayment(), 0.01);
        assertEquals(98.14, first.getPrincipal(), 0.01);
        assertEquals(4.17, first.getInterest(), 0.01);
        assertEquals(901.86, first.getRemainingBalance(), 0.01);

        // Expected Installment 10
        InstallmentDTO last = actual.get(9);
        assertEquals(10, last.getMonth());
        assertEquals(102.27, last.getMonthlyPayment(), 0.01);
        assertEquals(101.85, last.getPrincipal(), 0.01);
        assertEquals(0.42, last.getInterest(), 0.01);
        assertEquals(0.00, last.getRemainingBalance(), 0.01);

    }

    @Test
    void testLoanCalculation_2500_2Percent_36Months_FirstAndLastInstallment() {
        double amount = 2500.0;
        double rate = 2.0;
        int months = 36;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> actual = response.getInstallments();

        assertEquals(36, actual.size(), "Should generate 36 installments");

        // First installment assertions
        InstallmentDTO first = actual.get(0);
        assertEquals(1, first.getMonth());
        assertEquals(71.61, first.getMonthlyPayment(), 0.01);
        assertEquals(67.44, first.getPrincipal(), 0.01);
        assertEquals(4.17, first.getInterest(), 0.01);
        assertEquals(2432.56, first.getRemainingBalance(), 0.01);

        // Last installment assertions
        InstallmentDTO last = actual.get(35);
        assertEquals(36, last.getMonth());
        assertEquals(71.50, last.getMonthlyPayment(), 0.01);
        assertEquals(71.38, last.getPrincipal(), 0.01);
        assertEquals(0.12, last.getInterest(), 0.01);
        assertEquals(0.00, last.getRemainingBalance(), 0.01);
    }

    @Test
    void testLoanCalculation_500_7Percent_3Months_FullInstallments() {
        double amount = 500.0;
        double rate = 7.0;
        int months = 3;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> actual = response.getInstallments();

        assertEquals(3, actual.size(), "Should generate 3 installments");

        // Expected installment details
        double[] expectedMonthlyPayments = {168.61, 168.61, 168.63};
        double[] expectedPrincipals = {165.69, 166.66, 167.65};
        double[] expectedInterests = {2.92, 1.95, 0.98};
        double[] expectedBalances = {334.31, 167.65, 0.0};

        for (int i = 0; i < 3; i++) {
            InstallmentDTO installment = actual.get(i);
            assertEquals(i + 1, installment.getMonth());
            assertEquals(expectedMonthlyPayments[i], installment.getMonthlyPayment(), 0.01, "Monthly payment mismatch at month " + (i + 1));
            assertEquals(expectedPrincipals[i], installment.getPrincipal(), 0.01, "Principal mismatch at month " + (i + 1));
            assertEquals(expectedInterests[i], installment.getInterest(), 0.01, "Interest mismatch at month " + (i + 1));
            assertEquals(expectedBalances[i], installment.getRemainingBalance(), 0.01, "Balance mismatch at month " + (i + 1));
        }
    }
}
