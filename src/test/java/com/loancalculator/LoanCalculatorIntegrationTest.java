package com.loancalculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loancalculator.dto.InstallmentDTO;
import com.loancalculator.dto.LoanRequestDTO;
import com.loancalculator.dto.LoanResponseDTO;
import com.loancalculator.repository.Installment;
import com.loancalculator.repository.LoanRequest;
import com.loancalculator.repository.LoanRequestRepository;
import com.loancalculator.service.LoanCalculatorService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class LoanCalculatorIntegrationTest {

    private final LoanCalculatorService service;
    private final LoanRequestRepository loanRequestRepository;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public LoanCalculatorIntegrationTest(LoanCalculatorService service, LoanRequestRepository loanRequestRepository, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.service = service;
        this.loanRequestRepository = loanRequestRepository;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }


    @Test
    @Transactional
    void testServiceAndRepositoryLayers() {
        double amount = 5000.00;
        double rate = 3.75;
        int months = 7;

        LoanResponseDTO response = service.calculateAndSaveLoan(amount, rate, months);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(7, installments.size());

        double[] expectedPayments = {723.24, 723.24, 723.24, 723.24, 723.24, 723.24, 723.26};
        double[] expectedPrincipals = {707.61, 709.83, 712.04, 714.27, 716.50, 718.74, 721.01};
        double[] expectedInterests = {15.63, 13.41, 11.20, 8.97, 6.74, 4.50, 2.25};
        double[] expectedBalances = {4292.39, 3582.56, 2870.52, 2156.25, 1439.75, 721.01, 0.00};

        for (int i = 0; i < months; i++) {
            InstallmentDTO inst = installments.get(i);
            assertEquals(i + 1, inst.getMonth());
            assertEquals(expectedPayments[i], inst.getMonthlyPayment(), 0.01);
            assertEquals(expectedPrincipals[i], inst.getPrincipal(), 0.01);
            assertEquals(expectedInterests[i], inst.getInterest(), 0.01);
            assertEquals(expectedBalances[i], inst.getRemainingBalance(), 0.01);
        }

        LoanRequest savedLoan = loanRequestRepository.findByAmountAndAnnualInterestRateAndDurationMonths(amount, rate, months)
                .stream().findFirst().orElseThrow(() -> new AssertionError("Loan not found"));

        assertEquals(amount, savedLoan.getAmount());
        assertEquals(rate, savedLoan.getAnnualInterestRate());
        assertEquals(months, savedLoan.getDurationMonths());

        List<Installment> savedInstallments = savedLoan.getInstallments();

        assertEquals(months, savedInstallments.size(), "Number of saved installments mismatch");

        for (int i = 0; i < months; i++) {
            Installment inst = savedInstallments.get(i);
            assertEquals(i + 1, inst.getMonth());
            assertEquals(expectedPayments[i], inst.getMonthlyPayment(), 0.01);
            assertEquals(expectedPrincipals[i], inst.getPrincipal(), 0.01);
            assertEquals(expectedInterests[i], inst.getInterest(), 0.01);
            assertEquals(expectedBalances[i], inst.getRemainingBalance(), 0.01);
            assertEquals(savedLoan.getId(), inst.getLoanRequest().getId());
        }
    }

    @Test
    @Transactional
    void testAllLayers() throws Exception {
        double amount = 3000.0;
        double rate = 8.0;
        int months = 5;

        Map<String, Object> request = new HashMap<>();
        request.put("amount", amount);
        request.put("annualInterestRate", rate);
        request.put("durationMonths", months);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        LoanResponseDTO response = objectMapper.readValue(json, LoanResponseDTO.class);
        List<InstallmentDTO> installments = response.getInstallments();

        assertEquals(5, installments.size());

        // Expected values
        double[] payments = {612.05, 612.05, 612.05, 612.05, 612.06};
        double[] principals = {592.05, 596.00, 599.97, 603.97, 608.01};
        double[] interests = {20.00, 16.05, 12.08, 8.08, 4.05};
        double[] balances = {2407.95, 1811.95, 1211.98, 608.01, 0.00};

        for (int i = 0; i < months; i++) {
            InstallmentDTO inst = installments.get(i);
            assertEquals(i + 1, inst.getMonth());
            assertEquals(payments[i], inst.getMonthlyPayment(), 0.01);
            assertEquals(principals[i], inst.getPrincipal(), 0.01);
            assertEquals(interests[i], inst.getInterest(), 0.01);
            assertEquals(balances[i], inst.getRemainingBalance(), 0.01);
        }

        List<LoanRequest> loans = loanRequestRepository.findByAmountAndAnnualInterestRateAndDurationMonths(amount, rate, months);
        assertEquals(1, loans.size());

        LoanRequest savedLoan = loans.get(0);
        assertEquals(amount, savedLoan.getAmount());
        assertEquals(rate, savedLoan.getAnnualInterestRate());
        assertEquals(months, savedLoan.getDurationMonths());

        List<Installment> savedInstallments = savedLoan.getInstallments();
        assertEquals(5, savedInstallments.size());
    }

    @Test
    void testNegativeAmountValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(-1000.0, 5.0, 12);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Amount must be greater than 0"));
    }

    @Test
    void testZeroAmountValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(0, 5.0, 12);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Amount must be greater than 0"));
    }

    @Test
    void testNegativeRateValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(1000, -5, 12);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Interest rate cannot be negative"));
    }

    @Test
    void testZeroMonthsValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(1000, 12, 0);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Duration (months) must be at least 1"));
    }

    @Test
    void testNegativeMonthsValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(1000, 12, -5);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Duration (months) must be at least 1"));
    }

    @Test
    void testRateOverLimitValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(1000, 105, 10);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Interest rate must be realistic (0-100%)"));
    }

    @Test
    void testMonthsOverLimitValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(1000, 22, 700);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Duration must be realistic (max 50 years)"));
    }

    @Test
    void testAmountOverLimitValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(2000000, 22, 300);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].defaultMessage")
                        .value("Amount must not exceed 1,000,000"));
    }

}
