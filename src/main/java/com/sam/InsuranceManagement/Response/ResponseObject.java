package com.sam.InsuranceManagement.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sam.InsuranceManagement.DTO.customer.CustomerResponseDTO;
import com.sam.InsuranceManagement.DTO.city.CityResponseDTO;
import com.sam.InsuranceManagement.DTO.policy.PolicyResponseDTO;
import com.sam.InsuranceManagement.DTO.coverage.CoverageResponseDTO;
import com.sam.InsuranceManagement.DTO.policyType.PolicyTypeResponseDTO;
import com.sam.InsuranceManagement.Entity.PolicyType;
import com.sam.InsuranceManagement.Projection.PolicyBasicInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseObject {

    private String successMessage;
    private String failureMessage;

    private CustomerResponseDTO customerDTO;
    private List<CustomerResponseDTO> customerListDTO;

    private CityResponseDTO cityDTO;
    private List<CityResponseDTO> cityListDTO;

    private PolicyResponseDTO policyDTO;
    private List<PolicyResponseDTO> policyListDTO;

    private CoverageResponseDTO coverageDTO;
    private List<CoverageResponseDTO> coverageListDTO;

    private PolicyTypeResponseDTO policyTypeDTO;
    private List<PolicyTypeResponseDTO> policyTypeListDTO;

    // policyBasicList will store the output of that custom query,
    // avoiding full entity/DTO mapping.
    private List<PolicyBasicInfo> policyBasicList;

    // Add this new field to hold total premium
    private Double premiumTotal;

    //  Used to return raw custom data like projection/groupBy results
    private Object data;

    private Long customerCount;

    public ResponseObject() {
    }
}
