package com.loancalculator.edge;

import com.loancalculator.dto.LoanRequestDTO;
import com.loancalculator.dto.LoanResponseDTO;
import com.loancalculator.service.LoanCalculatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/loan")
public class LoanController {

    private final LoanCalculatorService service;

    @Autowired
    public LoanController(LoanCalculatorService service) {
        this.service = service;
    }


    @PostMapping("/calculate")
    public ResponseEntity<LoanResponseDTO> calculate(@Valid @RequestBody LoanRequestDTO request) {
        return ResponseEntity.ok(service.calculateAndSaveLoan(request.getAmount(), request.getAnnualInterestRate(), request.getDurationMonths()));
    }
}
