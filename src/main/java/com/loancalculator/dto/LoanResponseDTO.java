package com.loancalculator.dto;


import java.util.ArrayList;
import java.util.List;


public class LoanResponseDTO {
    private List<InstallmentDTO> installments = new ArrayList<>();

    public List<InstallmentDTO> getInstallments() {
        return installments;
    }

    public void setInstallments(List<InstallmentDTO> installments) {
        this.installments = installments;
    }
}
