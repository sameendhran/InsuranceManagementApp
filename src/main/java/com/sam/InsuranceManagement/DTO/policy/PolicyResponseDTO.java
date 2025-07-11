package com.sam.InsuranceManagement.DTO.policy;

import lombok.Data;

@Data
public class PolicyResponseDTO {
    private int policyId;
    private String policyNumber;
    private double premium;

    private int coverageId;
    private int policyTypeId;
    private int customerId;

    private String createdDate;
    private String updatedDate;
}
